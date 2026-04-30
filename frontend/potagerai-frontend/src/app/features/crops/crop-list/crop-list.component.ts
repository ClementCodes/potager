import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTooltipModule } from "@angular/material/tooltip";
import { CropService } from "../../../core/services/crop.service";
import { Crop } from "../../../core/models/crop.model";

@Component({
  selector: "app-crop-list",
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatIconModule,
    MatProgressSpinnerModule, MatTooltipModule
  ],
  templateUrl: "./crop-list.component.html",
  styleUrl: "./crop-list.component.scss"
})
export class CropListComponent implements OnInit {
  private cropService = inject(CropService);

  crops: Crop[] = [];
  loading = true;

  ngOnInit(): void {
    this.cropService.findAll().subscribe({
      next: (data) => { this.crops = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  andiColor(score: number): string {
    const t = Math.min(score / 1000, 1);
    const r = Math.round(255 * (1 - t));
    const g = Math.round(136 + 119 * t);
    return `rgba(${r},${g},0,0.18)`;
  }
}
