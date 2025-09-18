import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';
import { landingPage } from './pages/landingPage/landingPage.component';
import { HealthResourcesComponent } from './pages/health-resources/health-resources.component';
import { BigFiveTestComponent } from './pages/big-five-test/big-five-test.component';
import { CommunityComponent } from './pages/community/community.component';
import { NewPostComponent } from './pages/community/new-post/new-post.component';
import { MeditationsComponent } from './pages/Meditations/Meditations.component';
import { AppointmentComponent } from './pages/appointment/appointment.component';
import { PatientsHistorialComponent } from './pages/pacientsHistorial/pacientsHistorial.component';
import { DoctorAppointmentsComponent } from './pages/doctor-appointments/doctor-appointments.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';

export const routes: Routes = [
  { path: '', component: landingPage },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'home', component: landingPage },
  { path: 'resources', component: HealthResourcesComponent },
  { path: 'bigfive', component: BigFiveTestComponent },
  { 
    path: 'community',
    component: CommunityComponent,
    children: [
      { path: 'new', component: NewPostComponent },
      { path: '', redirectTo: 'list', pathMatch: 'full' }
    ]
  },
  { path: 'meditations', component: MeditationsComponent },
  { path: 'appointments', component: AppointmentComponent },
  { path: 'historials', component: PatientsHistorialComponent },
  { path: 'doctor-appointments', component: DoctorAppointmentsComponent },
  { path: 'admin', component: AdminPageComponent }
];