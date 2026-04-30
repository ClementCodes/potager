import { Component, OnInit, ElementRef, ViewChild, inject } from "@angular/core";
import { CommonModule, DecimalPipe } from "@angular/common";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatChipsModule } from "@angular/material/chips";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { GardenService } from "../../../core/services/garden.service";
import { OptimizationResult, PlotAllocation } from "../../../core/models/optimization.model";
import * as d3 from "d3";

const FAMILY_EMOJI: Record<string, string> = {
  Solanaceae:      "🍅",
  Cucurbitaceae:   "🥒",
  Apiaceae:        "🥕",
  Fabaceae:        "🫘",
  Amaranthaceae:   "🌿",
  Amaryllidaceae:  "🧅",
  Asteraceae:      "🥬",
  Brassicaceae:    "🥦",
  Poaceae:         "🌽",
  Convolvulaceae:  "🍠",
  Caprifoliaceae:  "🥗",
};

@Component({
  selector: "app-garden-result",
  standalone: true,
  imports: [
    CommonModule, RouterLink, DecimalPipe,
    MatCardModule, MatButtonModule, MatIconModule,
    MatProgressBarModule, MatChipsModule, MatProgressSpinnerModule
  ],
  templateUrl: "./garden-result.component.html",
  styleUrl: "./garden-result.component.scss"
})
export class GardenResultComponent implements OnInit {
  @ViewChild("treemap") treemapRef!: ElementRef<SVGElement>;

  private route = inject(ActivatedRoute);
  private gardenService = inject(GardenService);

  result: OptimizationResult | null = null;
  loading = true;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get("id"));
    // L'id de la route est l'id du JARDIN (pas de l'OptimizationResult).
    // On tente d'abord de récupérer le dernier résultat persisté (GET), et on
    // ne relance une optimisation (POST) qu'en dernier recours (404).
    this.gardenService.getLatestOptimization(id).subscribe({
      next: (r) => {
        this.result = r;
        this.loading = false;
        setTimeout(() => this.renderTreemap(), 100);
      },
      error: (err) => {
        if (err?.status === 404) {
          this.gardenService.optimize(id).subscribe({
            next: (r) => {
              this.result = r;
              this.loading = false;
              setTimeout(() => this.renderTreemap(), 100);
            },
            error: () => { this.loading = false; }
          });
        } else {
          this.loading = false;
        }
      }
    });
  }

  getTotalSurface(): number {
    return this.result?.plotAllocations.reduce((s, a) => s + a.allocatedSurfaceM2, 0) ?? 0;
  }

  getSurfacePercent(a: PlotAllocation): number {
    const total = this.getTotalSurface();
    return total > 0 ? (a.allocatedSurfaceM2 / total) * 100 : 0;
  }

  getPlantCount(a: PlotAllocation): number | null {
    if (!a.plantSpacingM2 || a.plantSpacingM2 <= 0) return null;
    return Math.floor(a.allocatedSurfaceM2 / a.plantSpacingM2);
  }

  /**
   * Calcule un agencement en rangées (lignes × colonnes) pour le plan visuel.
   * On suppose une parcelle quasi-carrée : cols ≈ √N, rows = ceil(N / cols).
   * Plafonné à MAX_DISPLAY plants pour éviter de saturer le DOM.
   */
  getPlantLayout(a: PlotAllocation): { rows: number; cols: number; total: number; displayed: number } | null {
    const total = this.getPlantCount(a);
    if (total == null || total <= 0) return null;
    const MAX_DISPLAY = 150;
    const displayed = Math.min(total, MAX_DISPLAY);
    const cols = Math.max(1, Math.round(Math.sqrt(displayed)));
    const rows = Math.ceil(displayed / cols);
    return { rows, cols, total, displayed };
  }

  /** Itérables pour @for dans le template (Angular 18 control flow). */
  range(n: number): number[] {
    return Array.from({ length: Math.max(0, n) }, (_, i) => i);
  }

  /** Hauteur visuelle d'une bande (en px) proportionnelle à la part de surface. */
  getStripHeight(a: PlotAllocation): number {
    const pct = this.getSurfacePercent(a);
    // mini 60 px, max 220 px ; sinon proportionnel
    return Math.max(60, Math.min(220, Math.round(pct * 8)));
  }

  getEmoji(a: PlotAllocation): string {
    return FAMILY_EMOJI[a.botanicalFamily ?? ""] ?? "🌱";
  }

  getAndiColor(score: number): string {
    // Gradient vert clair → vert foncé selon score ANDI (0–1000)
    const t = Math.min(score / 1000, 1);
    const lightness = Math.round(55 - t * 30);
    return `hsl(120, 55%, ${lightness}%)`;
  }

  andiColor(score: number): string {
    const t = Math.min(score / 1000, 1);
    const r = Math.round(255 * (1 - t));
    const g = Math.round(136 + 119 * t);
    return `rgba(${r},${g},0,0.2)`;
  }

  private renderTreemap(): void {
    if (!this.result || !this.treemapRef) return;

    const el = this.treemapRef.nativeElement;
    const width = el.clientWidth || 800;
    const height = 480;

    d3.select(el).selectAll("*").remove();

    const svg = d3.select(el)
      .attr("viewBox", `0 0 ${width} ${height}`)
      .attr("preserveAspectRatio", "xMidYMid meet");

    const allocations = this.result.plotAllocations;
    const data = {
      name: "root",
      children: allocations.map((a: PlotAllocation) => ({
        name: a.cropName,
        value: a.allocatedSurfaceM2,
        andi: a.andiScore ?? 0,
        kcal: a.estimatedCalories,
        surface: a.allocatedSurfaceM2
      }))
    };

    const root = d3.treemap<any>()
      .size([width, height])
      .paddingInner(3)
      .paddingOuter(4)
      .round(true)(
        d3.hierarchy(data)
          .sum((d: any) => d.value ?? 0)
          .sort((a, b) => (b.value ?? 0) - (a.value ?? 0))
      );

    const maxAndi: number = (d3.max(allocations, (d: PlotAllocation) => d.andiScore) as number) ?? 1000;
    const colorScale = d3.scaleSequential(d3.interpolateYlGn).domain([0, maxAndi]);

    const cell = svg.selectAll("g")
      .data(root.leaves())
      .join("g")
      .attr("transform", (d: any) => `translate(${d.x0},${d.y0})`);

    cell.append("rect")
      .attr("width", (d: any) => d.x1 - d.x0)
      .attr("height", (d: any) => d.y1 - d.y0)
      .attr("fill", (d: any) => colorScale(d.data.andi as number))
      .attr("rx", 4)
      .attr("stroke", "#fff")
      .attr("stroke-width", 1);

    cell.append("text")
      .attr("x", 8).attr("y", 20)
      .attr("font-size", "13px").attr("font-weight", "600").attr("fill", "#222")
      .text((d: any) => (d.x1 - d.x0) > 60 ? d.data.name : "");

    cell.append("text")
      .attr("x", 8).attr("y", 36)
      .attr("font-size", "11px").attr("fill", "#444")
      .text((d: any) => (d.x1 - d.x0) > 80 && (d.y1 - d.y0) > 50
        ? `${(d.data.surface as number).toFixed(1)} m2` : "");

    cell.append("title")
      .text((d: any) =>
        `${d.data.name}\n${(d.data.surface as number).toFixed(1)} m2\n` +
        `${Math.round(d.data.kcal as number).toLocaleString()} kcal/an\n` +
        `ANDI: ${Math.round(d.data.andi as number)}`
      );
  }
}
