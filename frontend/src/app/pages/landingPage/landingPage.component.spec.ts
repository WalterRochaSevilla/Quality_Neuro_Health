import { ComponentFixture, TestBed } from '@angular/core/testing';

import { landingPage } from './landingPage.component';

describe('landingPage', () => {
  let component: landingPage;
  let fixture: ComponentFixture<landingPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [landingPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(landingPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
