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
import { debounceTime, distinctUntilChanged, filter, Subject, takeUntil } from "rxjs";
import { GardenService } from "../../../core/services/garden.service";
import { ClimateZoneService } from "../../../core/services/climate-zone.service";
import { ClimateZone } from "../../../core/models/climate-zone.model";
import { CreateGardenRequest, SurfaceEstimate } from "../../../core/models/garden.model";

@Component({
  selector: "app-garden-form",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatSnackBarModule, MatProgressSpinnerModule, MatIconModule
  ],
  templateUrl: "./garden-form.component.html",
  styleUrl: "./garden-form.component.scss"
})
export class GardenFormComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private gardenService = inject(GardenService);
  private climateZoneService = inject(ClimateZoneService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private destroy$ = new Subject<void>();

  climateZones: ClimateZone[] = [];

  /** Formulaire en 2 blocs : foyer (étape 1) + surface (étape 2) */
  form = this.fb.group({
    householdSize:   [null as number | null, [Validators.required, Validators.min(1), Validators.max(20)]],
    climateZoneCode: [null as string | null, Validators.required],
    totalSurfaceM2:  [null as number | null, [Validators.required, Validators.min(1)]]
  });

  loading = false;
  estimating = false;
  surfaceEstimate: SurfaceEstimate | null = null;

  optimizationError: {
    requiredSurfaceM2: number;
    currentSurfaceM2: number;
    persons: number;
    zoneName: string;
  } | null = null;

  ngOnInit(): void {
    this.climateZoneService.findAll().subscribe({
      next: (zones) => (this.climateZones = zones),
      error: () => this.snackBar.open("Impossible de charger les zones climatiques", "Fermer", { duration: 4000 })
    });

    // Quand householdSize + climateZoneCode sont remplis → fetch l'estimation
    this.form.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged((a, b) =>
        a.householdSize === b.householdSize && a.climateZoneCode === b.climateZoneCode),
      filter(() => {
        const h = this.form.get("householdSize");
        const z = this.form.get("climateZoneCode");
        return !!(h?.valid && z?.valid);
      }),
      takeUntil(this.destroy$)
    ).subscribe(() => this.fetchEstimate());
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
        // Pré-remplit la surface si le champ est encore vide
        if (!this.form.value.totalSurfaceM2) {
          this.form.patchValue({ totalSurfaceM2: est.estimatedSurfaceM2 }, { emitEvent: false });
        }
      },
      error: () => { this.estimating = false; }
    });
  }

  /** Surface saisie vs surface estimée : retourne 'ok' | 'warning' | 'unknown' */
  get surfaceStatus(): "ok" | "warning" | "unknown" {
    if (!this.surfaceEstimate || !this.form.value.totalSurfaceM2) return "unknown";
    return this.form.value.totalSurfaceM2 >= this.surfaceEstimate.estimatedSurfaceM2 ? "ok" : "warning";
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
        this.gardenService.optimize(garden.id).subscribe({
          next: () => this.router.navigate(["/garden", garden.id, "result"]),
          error: (err) => {
            this.loading = false;
            if (err.status === 422) {
              const needed: number = err.error?.requiredSurfaceM2;
              const zoneName = this.climateZones.find(
                z => z.code === this.form.value.climateZoneCode
              )?.name ?? 'cette zone';
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
        this.snackBar.open("Erreur lors de la creation du jardin", "Fermer", { duration: 4000 });
      }
    });
  }
}
