import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HealthResourcesComponent } from './health-resources.component';

describe('HealthResourcesComponent', () => {
  let component: HealthResourcesComponent;
  let fixture: ComponentFixture<HealthResourcesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HealthResourcesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HealthResourcesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
