import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BigFiveTestComponent } from './big-five-test.component';

describe('BigFiveTestComponent', () => {
  let component: BigFiveTestComponent;
  let fixture: ComponentFixture<BigFiveTestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BigFiveTestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BigFiveTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
