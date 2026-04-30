import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('devrait être créé', () => {
    expect(service).toBeTruthy();
  });

  it('isAuthenticated() retourne false sans token', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('isAuthenticated() retourne true après stockage d un token', () => {
    localStorage.setItem('token', 'fake.token.here');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('getToken() retourne null sans token', () => {
    expect(service.getToken()).toBeNull();
  });

  it('logout() supprime le token du localStorage', () => {
    localStorage.setItem('token', 'fake.token.here');
    service.logout();
    expect(localStorage.getItem('token')).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('login() appelle POST /auth/login et stocke le token', () => {
    const mockResponse = {
      token: 'test.jwt.token',
      tokenType: 'Bearer',
      userId: 1,
      email: 'test@potagerai.com'
    };

    service.login({ email: 'test@potagerai.com', password: 'Test1234!' }).subscribe(res => {
      expect(res.token).toBe('test.jwt.token');
      expect(service.isAuthenticated()).toBeTrue();
      expect(service.getToken()).toBe('test.jwt.token');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('register() appelle POST /auth/register', () => {
    const mockResponse = {
      token: 'new.jwt.token',
      tokenType: 'Bearer',
      userId: 2,
      email: 'nouveau@potagerai.com'
    };

    service.register({ email: 'nouveau@potagerai.com', password: 'Test1234!' }).subscribe(res => {
      expect(res.email).toBe('nouveau@potagerai.com');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
