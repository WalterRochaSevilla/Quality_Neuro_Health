// navigation.service.ts
import { Injectable } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  private fragmentSubscription: Subscription | undefined;

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  setupFragmentNavigation(): void {
    this.fragmentSubscription = this.route.fragment.subscribe(fragment => {
      if (fragment) {
        setTimeout(() => this.scrollToFragment(fragment), 100);
      }
    });
  }

  private scrollToFragment(fragment: string): void {
    try {
      const element = document.querySelector(`#${fragment}`);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    } catch (e) {
      console.error('Error scrolling to fragment:', e);
    }
  }

  navigateWithDate(date: Date, datePipe: any): void {
    const formattedDate = datePipe.transform(date, 'yyyy-MM-dd');
    this.router.navigate(['/appointments'], {
      queryParams: { date: formattedDate }
    });
  }

  cleanup(): void {
    if (this.fragmentSubscription) {
      this.fragmentSubscription.unsubscribe();
    }
  }
}