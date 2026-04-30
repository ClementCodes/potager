import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from "@angular/core";
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
export class GardenResultComponent implements OnInit, AfterViewInit {
  @ViewChild("treemap") treemapRef!: ElementRef<SVGElement>;

  result: OptimizationResult | null = null;
  loading = true;

  constructor(private route: ActivatedRoute, private gardenService: GardenService) {}

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

  ngAfterViewInit(): void {}

  getTotalSurface(): number {
    return this.result?.plotAllocations.reduce((s: number, a: PlotAllocation) => s + a.allocatedSurfaceM2, 0) ?? 0;
  }

  getSurfacePercent(a: PlotAllocation): number {
    const total = this.getTotalSurface();
    return total > 0 ? (a.allocatedSurfaceM2 / total) * 100 : 0;
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
