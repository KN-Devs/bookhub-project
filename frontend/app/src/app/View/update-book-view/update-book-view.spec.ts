import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateBookView } from './update-book-view';

describe('UpdateBookView', () => {
  let component: UpdateBookView;
  let fixture: ComponentFixture<UpdateBookView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateBookView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateBookView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
