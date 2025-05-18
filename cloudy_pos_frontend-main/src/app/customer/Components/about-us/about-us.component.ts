import { AfterViewInit, Component, OnInit } from '@angular/core';
import { AnimationService } from 'app/Common/services/animation.service';
import L, * as l from 'leaflet';
@Component({
  selector: 'app-about-us',
  imports: [],
  templateUrl: './about-us.component.html',
  styleUrl: './about-us.component.css',
})
export class AboutUsComponent implements AfterViewInit {
  private map: L.Map | undefined;
  constructor(private animationService: AnimationService) {}
  private tileLayer: L.TileLayer | undefined;

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.initMap();
      this.initAnimations();
    }, 300); // Increased delay to 300ms for better DOM readiness
  }

  private initMap(): void {
    const mapContainer = document.getElementById('map');
    if (!mapContainer) {
      console.error('Map container not found');
      return;
    }

    // Fix Leaflet's default icon paths (swapped iconUrl and iconRetinaUrl)
    const iconUrl = 'assets/images/marker-icon.png'; // Standard icon
    const iconRetinaUrl = 'assets/images/marker-icon-2x.png'; // 2x icon for retina displays
    const shadowUrl = 'assets/images/marker-shadow.png';

    const customIcon = L.icon({
      iconUrl,
      iconRetinaUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41],
    });

    // Initialize the map
    this.map = L.map('map', {
      center: [51.505, -0.09],
      zoom: 15,
      scrollWheelZoom: false,
    });

    // Add OpenStreetMap tile layer and store it
    this.tileLayer = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        attribution:
          '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 19,
        tileSize: 256,
        zoomOffset: 0,
      },
    ).addTo(this.map);

    // Define branches with coordinates
    const branches = [
      {
        name: 'Downtown Store',
        coords: [51.505, -0.09],
        address: '123 Main St, Cityville',
      },
      {
        name: 'Westside Store',
        coords: [51.51, -0.1],
        address: '456 Oak Ave, West City',
      },
      {
        name: 'Eastside Store',
        coords: [51.5, -0.08],
        address: '789 Pine Rd, East Town',
      },
    ];

    // Add markers for each branch with the custom icon
    branches.forEach((branch) => {
      if (branch.coords.length === 2) {
        L.marker(branch.coords as L.LatLngTuple, { icon: customIcon })
          .addTo(this.map!)
          .bindPopup(`<b>${branch.name}</b><br>${branch.address}`);
      } else {
        console.error('Invalid coordinates for branch:', branch);
      }
    });

    // Fit the map to the bounds of all markers
    const markerBounds = L.latLngBounds(
      branches.map((branch) => branch.coords as L.LatLngTuple),
    );
    this.map.fitBounds(markerBounds, { padding: [50, 50] });

    // Force map to resize and redraw tiles
    setTimeout(() => {
      if (this.map && this.tileLayer) {
        this.map.invalidateSize();
        this.tileLayer.redraw(); // Force tile redraw
      }
    }, 1000); // Increased delay to ensure DOM stability

    // Handle window resize
    window.addEventListener('resize', () => {
      if (this.map && this.tileLayer) {
        this.map.invalidateSize();
        this.tileLayer.redraw();
      }
    });
  }

  refreshMap(): void {
    if (this.map && this.tileLayer) {
      this.map.invalidateSize();
      const branches = [
        { coords: [51.505, -0.09] },
        { coords: [51.51, -0.1] },
        { coords: [51.5, -0.08] },
      ];
      const markerBounds = L.latLngBounds(
        branches.map((branch) => branch.coords as L.LatLngTuple),
      );
      this.map.fitBounds(markerBounds, { padding: [50, 50] });
      this.tileLayer.redraw(); // Force tile redraw
    }
  }

  private initAnimations(): void {
    // Hero Section Animations
    this.animationService.animateHeroTitle('.hero-title', { duration: 1.5 });
    this.animationService.scrollFadeIn('.hero-subtitle', {
      duration: 1.2,
      y: 30,
    });
    this.animationService.pulse('.cta-button', {
      duration: 0.8,
      scale: 1.05,
      repeat: 2,
    });

    // Features Section Animations
    this.animationService.scrollFadeIn('.features-section .section-title', {
      y: 30,
    });
    this.animationService.staggerScrollAppear('.feature-card', {
      duration: 0.8,
      y: 40,
      stagger: 0.3,
    });

    // History Section Animations
    this.animationService.scrollFadeIn('.history-section .section-title', {
      y: 30,
    });
    this.animationService.staggerScrollAppear('.timeline-item', {
      duration: 0.8,
      y: 40,
      stagger: 0.3,
    });

    // Store Locator Animations
    this.animationService.scrollFadeIn(
      '.store-locator-section .section-title',
      { y: 30 },
    );
    // Removed scrollZoomIn animation to prevent interference with map rendering
    this.animationService.staggerScrollAppear('.branch-card', {
      duration: 0.8,
      y: 40,
      stagger: 0.3,
    });

    // CTA Section Animations
    this.animationService.scrollFadeIn('.cta-section .section-title', {
      y: 30,
    });
    this.animationService.scrollSlideInFromBottom('.cta-section p', {
      distance: 50,
    });
    this.animationService.pulse('.cta-section .cta-button', {
      duration: 0.8,
      scale: 1.05,
      repeat: 2,
    });
  }
}
