import { ComponentFixture, TestBed } from '@angular/core/testing'
import { Type } from '@angular/core'
import { vi } from 'vitest'

export async function createComponent<T>(
  component: Type<T>,
  providers: any[] = []
): Promise<{ fixture: ComponentFixture<T>; component: T }> {
  const isStandalone = (component as any).ɵcmp?.standalone ?? false

  await TestBed.configureTestingModule({
    ...(isStandalone ? { imports: [component] } : { declarations: [component] }),
    providers,
  }).compileComponents()

  const fixture = TestBed.createComponent(component)
  const componentInstance = fixture.componentInstance
  fixture.detectChanges()

  return { fixture, component: componentInstance }
}

export class MockService {
  public readonly isMock = true

  asObservable(data: any = null) {
    return {
      subscribe: (callback: (data: any) => void) => {
        callback(data)
        return { unsubscribe: () => {} }
      },
    }
  }

  asPromise(data: any = null) {
    return Promise.resolve(data)
  }
}

export function setupServiceTest<T>(
  serviceClass: Type<T>,
  providers: any[] = []
): T {
  TestBed.configureTestingModule({
    providers: [serviceClass, ...providers],
  })
  return TestBed.inject(serviceClass)
}


export class MockHttpService extends MockService {
  get = vi.fn(() => this.asObservable())
  post = vi.fn(() => this.asObservable())
  put = vi.fn(() => this.asObservable())
  delete = vi.fn(() => this.asObservable())
}

export class MockRouterService extends MockService {
  navigate = vi.fn(() => Promise.resolve(true))
  navigateByUrl = vi.fn(() => Promise.resolve(true))
  events = this.asObservable()
}