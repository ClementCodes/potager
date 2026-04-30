import { Component, inject } from "@angular/core";
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
export class GardenFormComponent {
  private fb = inject(FormBuilder);
  private gardenService = inject(GardenService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  climateZones = [
    { code: "FR-OCC", label: "Occitanie (x1.00)" },
    { code: "FR-CON", label: "Continental (x0.85)" },
    { code: "FR-MED", label: "Mediterraneen (x1.20)" },
    { code: "FR-MON", label: "Montagnard (x0.65)" }
  ];

  form = this.fb.group({
    totalSurfaceM2: [null as number | null, [Validators.required, Validators.min(1)]],
    householdSize:  [null as number | null, [Validators.required, Validators.min(1), Validators.max(20)]],
    climateZoneCode: [null as string | null, Validators.required]
  });

  loading = false;

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
          next: (result) => this.router.navigate(["/garden", result.id, "result"]),
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
