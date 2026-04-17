import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailBookView } from './detail-book-view';

describe('DetailBookView', () => {
  let component: DetailBookView;
  let fixture: ComponentFixture<DetailBookView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailBookView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailBookView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
