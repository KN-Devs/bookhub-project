import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllBooksView } from './all-books-view';

describe('AllBooksView', () => {
  let component: AllBooksView;
  let fixture: ComponentFixture<AllBooksView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllBooksView],
    }).compileComponents();

    fixture = TestBed.createComponent(AllBooksView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
