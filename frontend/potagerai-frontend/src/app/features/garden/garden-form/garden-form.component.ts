import { Component, inject, OnInit, OnDestroy } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatButtonModule } from "@angular/material/button";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatIconModule } from "@angular/material/icon";
import { MatChipsModule } from "@angular/material/chips";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { debounceTime, distinctUntilChanged, filter, Subject, switchMap, takeUntil } from "rxjs";
import { GardenService } from "../../../core/services/garden.service";
import { ClimateZoneService } from "../../../core/services/climate-zone.service";
import { CropService, Season } from "../../../core/services/crop.service";
import { ClimateZone } from "../../../core/models/climate-zone.model";
import { Crop } from "../../../core/models/crop.model";
import { CreateGardenRequest, SurfaceEstimate } from "../../../core/models/garden.model";

export const SEASON_OPTIONS: { value: Season; label: string; icon: string }[] = [
  { value: "TOUTE_ANNEE", label: "Toute l'année",  icon: "🌍" },
  { value: "PRINTEMPS",   label: "Printemps",       icon: "🌸" },
  { value: "ETE",         label: "Été",             icon: "☀️" },
  { value: "AUTOMNE",     label: "Automne",         icon: "🍂" },
  { value: "HIVER",       label: "Hiver",           icon: "❄️" },
];

@Component({
  selector: "app-garden-form",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatSnackBarModule, MatProgressSpinnerModule, MatIconModule,
    MatChipsModule, MatCheckboxModule
  ],
  templateUrl: "./garden-form.component.html",
  styleUrl: "./garden-form.component.scss"
})
export class GardenFormComponent implements OnInit, OnDestroy {
  private fb             = inject(FormBuilder);
  private gardenService  = inject(GardenService);
  private climateService = inject(ClimateZoneService);
  private cropService    = inject(CropService);
  private router         = inject(Router);
  private snackBar       = inject(MatSnackBar);
  private destroy$       = new Subject<void>();

  climateZones: ClimateZone[]  = [];
  availableCrops: Crop[]       = [];
  selectedCropIds: Set<number> = new Set();
  loadingCrops                 = false;
  readonly seasonOptions       = SEASON_OPTIONS;

  form = this.fb.group({
    householdSize:   [null as number | null, [Validators.required, Validators.min(1), Validators.max(20)]],
    climateZoneCode: [null as string | null, Validators.required],
    totalSurfaceM2:  [null as number | null, [Validators.required, Validators.min(1)]],
    season:          ["TOUTE_ANNEE" as Season, Validators.required]
  });

  loading             = false;
  estimating          = false;
  surfaceEstimate: SurfaceEstimate | null = null;

  optimizationError: {
    requiredSurfaceM2: number;
    currentSurfaceM2: number;
    persons: number;
    zoneName: string;
  } | null = null;

  ngOnInit(): void {
    this.climateService.findAll().subscribe({
      next: (zones) => (this.climateZones = zones),
      error: () => this.snackBar.open("Impossible de charger les zones climatiques", "Fermer", { duration: 4000 })
    });

    // Estimation de surface quand foyer + zone sont valides
    this.form.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged((a, b) =>
        a.householdSize === b.householdSize && a.climateZoneCode === b.climateZoneCode),
      filter(() => !!(this.form.get("householdSize")?.valid && this.form.get("climateZoneCode")?.valid)),
      takeUntil(this.destroy$)
    ).subscribe(() => this.fetchEstimate());

    // Rechargement des cultures quand la saison change
    this.form.get("season")!.valueChanges.pipe(
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe((season) => {
      if (season) this.loadCropsForSeason(season as Season);
    });

    // Chargement initial des cultures (toute l'année)
    this.loadCropsForSeason("TOUTE_ANNEE");
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private fetchEstimate(): void {
    const householdSize   = this.form.value.householdSize!;
    const climateZoneCode = this.form.value.climateZoneCode!;
    this.estimating = true;
    this.gardenService.estimateSurface(householdSize, climateZoneCode).subscribe({
      next: (est) => {
        this.surfaceEstimate = est;
        this.estimating = false;
        if (!this.form.value.totalSurfaceM2) {
          this.form.patchValue({ totalSurfaceM2: est.estimatedSurfaceM2 }, { emitEvent: false });
        }
      },
      error: () => { this.estimating = false; }
    });
  }

  private loadCropsForSeason(season: Season): void {
    this.loadingCrops = true;
    this.cropService.findBySeason(season).subscribe({
      next: (crops) => {
        this.availableCrops = crops;
        // Conserve uniquement les cultures encore disponibles dans la nouvelle saison
        const availableIds = new Set(crops.map(c => c.id));
        this.selectedCropIds = new Set([...this.selectedCropIds].filter(id => availableIds.has(id)));
        this.loadingCrops = false;
      },
      error: () => { this.loadingCrops = false; }
    });
  }

  toggleCrop(cropId: number): void {
    if (this.selectedCropIds.has(cropId)) {
      this.selectedCropIds.delete(cropId);
    } else {
      this.selectedCropIds.add(cropId);
    }
  }

  selectAll(): void {
    this.availableCrops.forEach(c => this.selectedCropIds.add(c.id));
  }

  clearAll(): void {
    this.selectedCropIds.clear();
  }

  get surfaceStatus(): "ok" | "warning" | "unknown" {
    if (!this.surfaceEstimate || !this.form.value.totalSurfaceM2) return "unknown";
    return this.form.value.totalSurfaceM2 >= this.surfaceEstimate.estimatedSurfaceM2 ? "ok" : "warning";
  }

  /** Cultures à envoyer au backend : si aucune sélection → toutes */
  private get cropIdsToSubmit(): number[] | undefined {
    return this.selectedCropIds.size > 0 ? [...this.selectedCropIds] : undefined;
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.optimizationError = null;

    const req: CreateGardenRequest = {
      totalSurfaceM2:  this.form.value.totalSurfaceM2!,
      householdSize:   this.form.value.householdSize!,
      climateZoneCode: this.form.value.climateZoneCode!,
      countryIsoCode:  "FRA"
    };

    this.gardenService.create(req).subscribe({
      next: (garden) => {
        this.gardenService.optimize(garden.id, this.cropIdsToSubmit).subscribe({
          next: () => this.router.navigate(["/garden", garden.id, "result"]),
          error: (err) => {
            this.loading = false;
            if (err.status === 422) {
              const needed: number = err.error?.requiredSurfaceM2;
              const zoneName = this.climateZones.find(
                z => z.code === this.form.value.climateZoneCode
              )?.name ?? "cette zone";
              this.optimizationError = {
                requiredSurfaceM2: needed,
                currentSurfaceM2: this.form.value.totalSurfaceM2 ?? 0,
                persons: this.form.value.householdSize ?? 1,
                zoneName
              };
            } else {
              this.snackBar.open(
                "Une erreur est survenue lors de l'optimisation. Réessayez.",
                "Fermer", { duration: 5000 }
              );
            }
          }
        });
      },
      error: () => {
        this.loading = false;
        this.snackBar.open("Erreur lors de la création du jardin", "Fermer", { duration: 4000 });
      }
    });
  }
}

