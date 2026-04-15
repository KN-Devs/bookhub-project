import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderView } from './header-view';

describe('HeaderView', () => {
  let component: HeaderView;
  let fixture: ComponentFixture<HeaderView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderView],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
