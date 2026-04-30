import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { AuthService } from "../../../core/services/auth.service";

@Component({
  selector: "app-register",
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatSnackBarModule, MatProgressSpinnerModule
  ],
  templateUrl: "./register.component.html",
  styleUrl: "./register.component.scss"
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  form = this.fb.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[A-Z])(?=.*\d).+$/)]]
  });
  loading = false;

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.authService.register(this.form.value as { email: string; password: string }).subscribe({
      next: () => {
        this.snackBar.open("Compte cree ! Bienvenue sur PotagerAI", "OK", { duration: 3000 });
        this.router.navigate(["/garden"]);
      },
      error: (err) => {
        this.loading = false;
        const msg = err.status === 409 ? "Email deja utilise" : "Erreur lors de la creation du compte";
        this.snackBar.open(msg, "Fermer", { duration: 4000, panelClass: "snack-error" });
      }
    });
  }
}
