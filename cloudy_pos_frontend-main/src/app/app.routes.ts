import { provideRouter, Routes } from '@angular/router';
import { HomeComponent } from './customer/Components/home/home.component';
import { roleGuard } from './guards/role/role.guard';
import { BarcodeScannerComponent } from './customer/Components/product/barcode-scanner/barcode-scanner.component';
import { ProductDetailComponent } from './customer/Components/product/product-detail/product-detail.component';
import { CartComponent } from './customer/Components/Purchase/cart/cart.component';
import { CheckoutComponent } from './customer/Components/Purchase/check-out/check-out.component';
import { PaypalReturnComponent } from './customer/Components/Purchase/paypal/paypal-return/paypal-return.component';
import { DiscountedProductsListsComponent } from './customer/Components/discounted-products-lists/discounted-products-lists.component';
import { LoyaltyPointsComponent } from './customer/Components/loyalty-points/loyalty-points.component';
import { CouponComponent } from './customer/Components/coupon/coupon.component';
import { ProfileComponent } from './customer/Components/profile/profile.component';
import { HistoryComponent } from './customer/Components/history/history.component';
import { ProductsComponent } from './customer/Components/products/products.component';
import { AboutUsComponent } from './customer/Components/about-us/about-us.component';
import { OffersComponent } from './customer/Components/offers/offers.component';
import { ContactUsComponent } from './customer/Components/contact-us/contact-us.component';
import { TermsComponent } from './customer/Components/terms/terms.component';
import { PrivacyComponent } from './customer/Components/privacy/privacy.component';
import { UnauthorizedComponent } from './Common/components/unauthorized/unauthorized.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' }, // Redirect root to 'home'
  { path: 'home', component: HomeComponent },
  { path: 'scan', component: BarcodeScannerComponent },
  {
    path: 'loyaltypoints',
    component: LoyaltyPointsComponent,
    canActivate: [roleGuard],
    data: { role: ['CUSTOMER'] },
  },
  {
    path: 'coupon',
    component: CouponComponent,
    canActivate: [roleGuard],
    data: { role: ['CUSTOMER'] },
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [roleGuard],
    data: { role: ['CUSTOMER'] },
  },
  {
    path: 'history',
    component: HistoryComponent,
    canActivate: [roleGuard],
    data: { role: ['CUSTOMER'] },
  },
  { path: 'products', component: ProductsComponent },
  { path: 'about', component: AboutUsComponent },
  { path: 'offers', component: OffersComponent },
  { path: 'contact', component: ContactUsComponent },
  { path: 'terms', component: TermsComponent },
  { path: 'privacy', component: PrivacyComponent },
  {
    path: 'discount/all',
    pathMatch: 'full',
    component: DiscountedProductsListsComponent,
  },
  {
    path: 'product/search/:barcode',
    component: ProductDetailComponent,
    pathMatch: 'full',
    runGuardsAndResolvers: 'always',
  },
  {
    path: 'cart',
    component: CartComponent,

    children: [
      {
        path: 'checkout', //  /cart/checkout
        component: CheckoutComponent,
      },
      {
        path: 'paypal',
        component: PaypalReturnComponent,
      },
      {
        path: 'payment-return', // Handle PayPal redirect with query params
        component: CartComponent, // Load CartComponent to manage stepper
        data: { step: 'payment' }, // Pass data to indicate Payment step
      },
    ],
  },
  {
    path: 'staff',
    loadChildren: () => import('./admin.routes').then((m) => m.adminRoutes),
  }, // Lazy loading for admin module
  { path: 'unauthorized', component: UnauthorizedComponent },
  { path: '**', redirectTo: 'unauthorized', pathMatch: 'full' },
];

export const routing = provideRouter(routes);
