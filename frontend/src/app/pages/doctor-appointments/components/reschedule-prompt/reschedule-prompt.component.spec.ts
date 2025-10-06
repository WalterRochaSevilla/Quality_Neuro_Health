import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReschedulePromptComponent } from './reschedule-prompt.component';

describe('ReschedulePromptComponent', () => {
  let component: ReschedulePromptComponent;
  let fixture: ComponentFixture<ReschedulePromptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReschedulePromptComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReschedulePromptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
