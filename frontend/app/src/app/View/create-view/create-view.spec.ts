import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateView } from './create-view';

describe('CreateView', () => {
  let component: CreateView;
  let fixture: ComponentFixture<CreateView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
