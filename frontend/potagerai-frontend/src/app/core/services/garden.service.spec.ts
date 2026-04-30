import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { GardenService } from './garden.service';
import { environment } from '../../../environments/environment';

describe('GardenService', () => {
  let service: GardenService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GardenService]
    });
    service = TestBed.inject(GardenService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait être créé', () => {
    expect(service).toBeTruthy();
  });

  it('create() appelle POST /gardens', () => {
    const request = {
      totalSurfaceM2: 150,
      householdSize: 2,
      climateZoneCode: 'FR-CON',
      countryIsoCode: 'FRA'
    };
    const mockGarden = { id: 1, totalSurfaceM2: 150, householdSize: 2 };

    service.create(request).subscribe(res => {
      expect(res.id).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gardens`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.totalSurfaceM2).toBe(150);
    req.flush(mockGarden);
  });

  it('findAll() appelle GET /gardens', () => {
    const mockGardens = [{ id: 1, totalSurfaceM2: 150 }, { id: 2, totalSurfaceM2: 80 }];

    service.findAll().subscribe(res => {
      expect(res.length).toBe(2);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gardens`);
    expect(req.request.method).toBe('GET');
    req.flush(mockGardens);
  });

  it('optimize() appelle POST /gardens/{id}/optimize', () => {
    const mockResult = {
      id: 1,
      gardenProfileId: 1,
      selfSufficiencyPercent: 87.5,
      totalCaloriesProduced: 1_642_500,
      plotAllocations: []
    };

    service.optimize(1).subscribe(res => {
      expect(res.selfSufficiencyPercent).toBe(87.5);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gardens/1/optimize`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResult);
  });
});
