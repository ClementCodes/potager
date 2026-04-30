import { Component, inject, OnInit } from "@angular/core";
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
import { GardenService } from "../../../core/services/garden.service";
import { ClimateZoneService } from "../../../core/services/climate-zone.service";
import { ClimateZone } from "../../../core/models/climate-zone.model";
import { CreateGardenRequest } from "../../../core/models/garden.model";

@Component({
  selector: "app-garden-form",
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatSnackBarModule, MatProgressSpinnerModule, MatIconModule
  ],
  templateUrl: "./garden-form.component.html",
  styleUrl: "./garden-form.component.scss"
})
export class GardenFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private gardenService = inject(GardenService);
  private climateZoneService = inject(ClimateZoneService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  climateZones: ClimateZone[] = [];

  form = this.fb.group({
    totalSurfaceM2: [null as number | null, [Validators.required, Validators.min(1)]],
    householdSize:  [null as number | null, [Validators.required, Validators.min(1), Validators.max(20)]],
    climateZoneCode: [null as string | null, Validators.required]
  });

  loading = false;

  ngOnInit(): void {
    this.climateZoneService.findAll().subscribe({
      next: (zones) => (this.climateZones = zones),
      error: () => this.snackBar.open("Impossible de charger les zones climatiques", "Fermer", { duration: 4000 })
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;

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
              const needed = err.error?.requiredSurfaceM2;
              const msg = needed
                ? "Surface insuffisante. Il vous faut au moins " + needed.toFixed(0) + " m2."
                : "Aucune solution realisable pour ces parametres.";
              this.snackBar.open(msg, "Fermer", { duration: 6000, panelClass: "snack-error" });
            } else {
              this.snackBar.open("Erreur lors de l optimisation", "Fermer", { duration: 4000 });
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
