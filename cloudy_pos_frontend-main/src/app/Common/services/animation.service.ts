import { ElementRef, Injectable, Renderer2 } from '@angular/core';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';

gsap.registerPlugin(ScrollTrigger);

@Injectable({
  providedIn: 'root',
})
export class AnimationService {
  private previousColor: string = '';
  constructor() {}

  // Existing Animations
  fadeIn(element: string | HTMLElement, duration = 1): void {
    gsap.from(element, {
      duration,
      opacity: 0,
      y: 50,
      ease: 'power2.out',
    });
  }

  slideInFromLeft(element: string | HTMLElement, duration = 1): void {
    gsap.from(element, {
      duration,
      x: -100,
      opacity: 0,
      ease: 'power2.out',
    });
  }

  staggerFadeIn(
    elements: string | HTMLElement[],
    duration = 1,
    stagger = 0.2,
  ): void {
    gsap.from(elements, {
      duration,
      opacity: 0,
      y: 50,
      stagger,
      ease: 'power2.out',
    });
  }
  /**
   * Preloader Reveal: Animates a preloader with SVG text and background, then fades out the overlay.
   * @param preloaderEl The preloader container element.
   * @param svgEl The SVG element containing text and background.
   * @param onComplete Callback to execute after animation completes (e.g., hide preloader).
   */
  preloaderReveal(
    preloaderEl: string | HTMLElement,
    svgEl: string | HTMLElement,
    onComplete?: () => void,
    options: { duration?: number; delay?: number } = {},
  ): void {
    const { duration = 2.5, delay = 0.5 } = options;
    const preloader =
      typeof preloaderEl === 'string'
        ? document.querySelector(preloaderEl)
        : preloaderEl;
    const svg =
      typeof svgEl === 'string' ? document.querySelector(svgEl) : svgEl;
    if (!preloader || !svg) return;

    const tl = gsap.timeline({
      onComplete,
    });

    // Initial state
    gsap.set(svg, {
      opacity: 0,
      scale: 0.9,
      // boxShadow: '0 0 0 rgba(11, 235, 235, 0)',
    });
    gsap.set(preloader, { opacity: 1 });

    // Animation sequence
    tl.to(svg, {
      opacity: 1,
      scale: 1,
      // boxShadow: '0 0 20px rgba(11, 235, 235, 0.7)',
      duration: duration * 0.4,
      delay,
      ease: 'power3.out',
    })
      .to(svg, {
        y: -15,
        duration: duration * 0.2,
        ease: 'sine.inOut',
        yoyo: true,
        repeat: 1,
      })
      .to(preloader, {
        opacity: 0,
        duration: duration * 0.4,
        ease: 'power2.in',
      });
  }
  // New Animations
  /**
   * Scroll Appear: Fades in an element as it enters the viewport.
   */
  scrollAppear(
    element: string | HTMLElement,
    options: { duration?: number; y?: number; start?: string } = {},
  ): void {
    const { duration = 1, y = 50, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none none',
      },
      duration,
      opacity: 0,
      y,
      ease: 'power3.out',
    });
  }

  /**
   * Scroll Zoom In: Zooms in an element as it scrolls into view.
   */
  scrollZoomIn(
    element: string | HTMLElement,
    options: { duration?: number; scaleFrom?: number; start?: string } = {},
  ): void {
    const { duration = 1, scaleFrom = 0.8, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none reverse',
      },
      duration,
      scale: scaleFrom,
      opacity: 0,
      ease: 'power3.out',
    });
  }

  /**
   * Scroll Zoom Out: Zooms out an element as it scrolls into view.
   */
  scrollZoomOut(
    element: string | HTMLElement,
    options: { duration?: number; scaleTo?: number; start?: string } = {},
  ): void {
    const { duration = 1, scaleTo = 1.2, start = 'top 80%' } = options;
    gsap.fromTo(
      element,
      {
        scale: 1,
        opacity: 1,
      },
      {
        scrollTrigger: {
          trigger: element,
          start,
          toggleActions: 'play none none reverse',
        },
        duration,
        scale: scaleTo,
        opacity: 0,
        ease: 'power3.out',
      },
    );
  }

  /**
   * Scroll Rotate: Rotates an element as it scrolls into view.
   */
  scrollRotate(
    element: string | HTMLElement,
    options: { duration?: number; rotation?: number; start?: string } = {},
  ): void {
    const { duration = 1, rotation = 360, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none none',
      },
      duration,
      rotation,
      opacity: 0,
      ease: 'power3.out',
    });
  }

  /**
   * Bounce In: A playful bounce effect for an element.
   */
  bounceIn(element: string | HTMLElement, duration = 1): void {
    gsap.from(element, {
      duration,
      y: -50,
      opacity: 0,
      ease: 'bounce.out',
    });
  }

  /**
   * Parallax Move: A subtle parallax effect on scroll.
   */
  parallaxMove(
    element: string | HTMLElement,
    options: { distance?: number; start?: string; end?: string } = {},
  ): void {
    const {
      distance = 100,
      start = 'top bottom',
      end = 'bottom top',
    } = options;
    gsap.fromTo(
      element,
      { y: -distance },
      {
        y: distance,
        ease: 'none',
        scrollTrigger: {
          trigger: element,
          start,
          end,
          scrub: true,
        },
      },
    );
  }

  /**
   * Pulse: A pulsating effect for emphasis.
   */
  pulse(
    element: string | HTMLElement,
    options: { duration?: number; scale?: number; repeat?: number } = {},
  ): void {
    const { duration = 1, scale = 1.1, repeat = 3 } = options; // Default to 3 repeats
    gsap.to(element, {
      duration,
      scale,
      repeat, // Limited repeats instead of infinite
      yoyo: true,
      ease: 'sine.inOut',
    });
  }

  /**
   * Staggered Scroll Appear: Staggered fade-in for multiple elements on scroll.
   */
  staggerScrollAppear(
    elements: string | HTMLElement[] | NodeListOf<HTMLElement>,
    options: {
      duration?: number;
      y?: number;
      stagger?: number;
      start?: string;
    } = {},
  ): void {
    const { duration = 1, y = 50, stagger = 0.2, start = 'top 80%' } = options;
    gsap.from(elements, {
      scrollTrigger: {
        trigger: elements,
        start,
        toggleActions: 'play none none none',
      },
      duration,
      opacity: 0,
      y,
      stagger,
      ease: 'power3.out',
    });
  }

  /**
   * Flip In: A flipping animation for an element entering the view.
   */
  flipIn(
    element: string | HTMLElement,
    options: { duration?: number; direction?: 'x' | 'y' } = {},
  ): void {
    const { duration = 1, direction = 'y' } = options;
    const rotationProp = direction === 'y' ? 'rotationY' : 'rotationX';
    gsap.from(element, {
      duration,
      [rotationProp]: 90,
      opacity: 0,
      ease: 'power3.out',
    });
  }

  /**
   * Animate Hero Title: A common animation for titles across the app.
   */
  animateHeroTitle(
    element: string | HTMLElement,
    options: { duration?: number; scaleFrom?: number } = {},
  ): void {
    const { duration = 1.5, scaleFrom = 0.8 } = options;
    gsap.from(element, {
      duration,
      opacity: 0,
      scale: scaleFrom,
      ease: 'elastic.out(1, 0.5)',
    });
  }

  /**
   * Glow Effect: Adds a glowing effect to an element as it scrolls into view.
   */
  glowEffect(
    element: string | HTMLElement,
    options: { duration?: number; start?: string; glowColor?: string } = {},
  ): void {
    const { duration = 1, start = 'top 80%', glowColor = '#ffd700' } = options;
    gsap.fromTo(
      element,
      {
        boxShadow: `0 0 0 0 ${glowColor}`,
        opacity: 0,
      },
      {
        scrollTrigger: {
          trigger: element,
          start,
          toggleActions: 'play none none none',
        },
        duration,
        boxShadow: `0 0 20px 5px ${glowColor}`,
        opacity: 1,
        ease: 'power3.out',
        yoyo: true,
        repeat: 1, // Glow fades in and out once
      },
    );
  }

  /**
   * Slide In From Left with ScrollTrigger
   */
  scrollSlideInFromLeft(
    element: string | HTMLElement,
    options: { duration?: number; distance?: number; start?: string } = {},
  ): void {
    const { duration = 1, distance = 100, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none reverse',
      },
      duration,
      x: -distance,
      opacity: 0,
      ease: 'power3.out',
    });
  }

  /**
   * Slide In From Right with ScrollTrigger
   */
  scrollSlideInFromRight(
    element: string | HTMLElement,
    options: { duration?: number; distance?: number; start?: string } = {},
  ): void {
    const { duration = 1, distance = 100, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none reverse',
      },
      duration,
      x: distance,
      opacity: 0,
      ease: 'power3.out',
    });
  }

  /**
   * Slide In From Bottom with ScrollTrigger
   */
  scrollSlideInFromBottom(
    element: string | HTMLElement,
    options: { duration?: number; distance?: number; start?: string } = {},
  ): void {
    const { duration = 1, distance = 100, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none reverse',
      },
      duration,
      y: distance,
      opacity: 0,
      ease: 'power3.out',
    });
  }
  // Continuous Scroll Animations with Scrub
  /**
   * Scrub Slide From Left: Element slides in from the left as the user scrolls.
   */
  scrubSlideFromLeft(
    element: string | HTMLElement,
    options: { distance?: number; start?: string; end?: string } = {},
  ): void {
    const { distance = 200, start = 'top 80%', end = 'bottom 20%' } = options;
    gsap.fromTo(
      element,
      { x: -distance, opacity: 0 },
      {
        x: 0,
        opacity: 1,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: element,
          start,
          end,
          scrub: 1, // Ties animation to scroll position (smooth scrubbing)
          toggleActions: 'play none none reverse', // Reverses animation when scrolling up
        },
      },
    );
  }

  /**
   * Scrub Slide From Right: Element slides in from the right as the user scrolls.
   */
  scrubSlideFromRight(
    element: string | HTMLElement,
    options: { distance?: number; start?: string; end?: string } = {},
  ): void {
    const { distance = 200, start = 'top 80%', end = 'bottom 20%' } = options;
    gsap.fromTo(
      element,
      { x: distance, opacity: 0 },
      {
        x: 0,
        opacity: 1,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: element,
          start,
          end,
          scrub: 1,
          toggleActions: 'play none none reverse',
        },
      },
    );
  }

  /**
   * Scrub Slide From Bottom: Element slides in from the bottom as the user scrolls.
   */
  scrubSlideFromBottom(
    element: string | HTMLElement,
    options: { distance?: number; start?: string; end?: string } = {},
  ): void {
    const { distance = 200, start = 'top 80%', end = 'bottom 20%' } = options;
    gsap.fromTo(
      element,
      { y: distance, opacity: 0 },
      {
        y: 0,
        opacity: 1,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: element,
          start,
          end,
          scrub: 1,
          toggleActions: 'play none none reverse',
        },
      },
    );
  }

  // Fade In for the Title (one-time animation, as it doesn't need to scrub)
  scrollFadeIn(
    element: string | HTMLElement,
    options: { duration?: number; y?: number; start?: string } = {},
  ): void {
    const { duration = 1, y = 50, start = 'top 80%' } = options;
    gsap.from(element, {
      scrollTrigger: {
        trigger: element,
        start,
        toggleActions: 'play none none reverse',
      },
      duration,
      opacity: 0,
      y,
      ease: 'power3.out',
    });
  }
  /**
   * Infinite Carousel: Animates a track of items to create an infinite scrolling effect.
   */
  infiniteCarousel(
    track: string | HTMLElement,
    options: {
      duration?: number;
      itemWidth?: number;
      totalItems?: number;
    } = {},
  ): void {
    const { duration = 20, itemWidth = 320, totalItems = 4 } = options; // itemWidth includes margin

    // Calculate the total width of the original set of items (before duplication)
    const totalWidth = itemWidth * totalItems;

    // Animate the track to move left continuously
    gsap.to(track, {
      x: -totalWidth, // Move the track to the left by the width of the original set
      duration: duration,
      ease: 'none', // Linear animation for smooth scrolling
      repeat: -1, // Infinite repeat
      modifiers: {
        x: gsap.utils.wrap(-totalWidth, 0), // Seamlessly loop the position
      },
    });
  }

  createInfiniteCarousel(
    container: HTMLElement,
    items: HTMLElement[],
    options: {
      speed: number;
      start: string;
      end: string;
      pauseOnHover?: boolean;
    },
  ) {
    // Calculate total width including margins
    const itemWidth = items[0].offsetWidth + 10; // 10px margin-right from CSS
    const totalWidth = itemWidth * items.length;

    // Clone items to ensure seamless looping
    const clones = items.map((item) => item.cloneNode(true) as HTMLElement);
    clones.forEach((clone) => container.appendChild(clone));

    const allItems = [...items, ...clones];
    gsap.set(allItems, { x: 0 });

    // Create the infinite scroll animation
    const animation = gsap.to(allItems, {
      x: -totalWidth,
      duration: totalWidth / 100 / options.speed, // Speed: pixels per second
      ease: 'none',
      repeat: -1,
      modifiers: {
        x: gsap.utils.unitize((x: number) => {
          return (x % totalWidth) + (x < -totalWidth ? totalWidth : 0);
        }),
      },
    });

    // ScrollTrigger to control visibility
    ScrollTrigger.create({
      trigger: container,
      start: options.start,
      end: options.end,
      onEnter: () => animation.play(),
      onLeave: () => animation.pause(),
      onEnterBack: () => animation.play(),
      onLeaveBack: () => animation.pause(),
    });

    // Pause on hover
    if (options.pauseOnHover) {
      const carousel = container.parentElement!;
      carousel.addEventListener('mouseenter', () => {
        gsap.to(animation, { timeScale: 0, duration: 0.3, ease: 'power2.out' });
      });
      carousel.addEventListener('mouseleave', () => {
        gsap.to(animation, { timeScale: 1, duration: 0.3, ease: 'power2.out' });
      });
    }

    // Responsive handling
    const handleResize = () => {
      const isSmallScreen = window.innerWidth <= 768;
      const newItemWidth = isSmallScreen
        ? window.innerWidth <= 480
          ? 70
          : 90
        : 110; // From CSS
      const newTotalWidth = newItemWidth * items.length;
      animation.duration(newTotalWidth / 100 / options.speed);
      animation.timeScale(isSmallScreen ? options.speed * 0.8 : options.speed);
    };

    window.addEventListener('resize', handleResize);
    handleResize();
  }
  /**
   * Scroll Hide/Show: Hides the header on scroll down and shows on scroll up or pause.
   */
  scrollHideShow(
    element: string | HTMLElement,
    options: {
      hideDistance?: string;
      duration?: number;
      scrollThreshold?: number;
      pauseDelay?: number;
    } = {},
    callback?: (isHidden: boolean) => void,
  ): () => void {
    const {
      hideDistance = '-100%',
      duration = 0.5,
      scrollThreshold = 100,
      pauseDelay = 1000,
    } = options;
    let lastScroll = 0;
    let scrollTimeout: any;

    const handleScroll = () => {
      const currentScroll = window.pageYOffset;

      // Clear existing timeout
      if (scrollTimeout) {
        clearTimeout(scrollTimeout);
      }

      // Scroll down: hide header
      if (currentScroll > lastScroll && currentScroll > scrollThreshold) {
        gsap.to(element, {
          y: hideDistance,
          duration,
          ease: 'power2.out',
          onComplete: () => callback?.(true),
        });
      }
      // Scroll up: show header
      else if (currentScroll < lastScroll) {
        gsap.to(element, {
          y: 0,
          duration,
          ease: 'power2.out',
          onComplete: () => callback?.(false),
        });
      }

      // Show header after pause
      scrollTimeout = setTimeout(() => {
        gsap.to(element, {
          y: 0,
          duration,
          ease: 'power2.out',
          onComplete: () => callback?.(false),
        });
      }, pauseDelay);

      lastScroll = currentScroll <= 0 ? 0 : currentScroll;
    };

    // Add scroll event listener
    window.addEventListener('scroll', handleScroll);

    // Return cleanup function
    return () => {
      window.removeEventListener('scroll', handleScroll);
      if (scrollTimeout) {
        clearTimeout(scrollTimeout);
      }
    };
  }
  /**
   * Banner Animation: Animates the banner image and text headers with scroll-triggered effects.
   */
  bannerAnimation(
    imageElement: string | HTMLElement,
    headerSelectors: { h1: string; h2: string; h3: string },
    triggerElement: string | HTMLElement,
    options: {
      imageScale?: number;
      imageWidth?: string;
      h1X?: string;
      h2X?: string;
      h3Y?: string;
      start?: string;
      end?: string;
      scrub?: number;
    } = {},
  ): void {
    const {
      imageScale = 1,
      imageWidth = '70%',
      h1X = '30%',
      h2X = '-50%',
      h3Y = '50%',
      start = 'top center',
      end = 'bottom center',
      scrub = 3,
    } = options;

    // Image scaling animation
    gsap.to(imageElement, {
      scale: imageScale,
      width: imageWidth,
      zIndex: 1,
      scrollTrigger: {
        trigger: triggerElement,
        start,
        end,
        scrub,
        onUpdate: (self) => {
          // Dynamically adjust z-index based on scroll progress
          const zIndexValue = self.progress < 0.5 ? 1 : -1; // Headers behind initially, then come forward
          gsap.set(
            [headerSelectors.h1, headerSelectors.h2, headerSelectors.h3],
            { zIndex: zIndexValue },
          );
        },
      },
    });

    // Text header animations
    gsap.to(headerSelectors.h1, {
      x: h1X,
      opacity: 1, // Fade in smoothly
      ease: 'power2.inOut', // Smooth easing
      zIndex: 2,
      scrollTrigger: {
        trigger: triggerElement,
        start,
        end,
        scrub: 2,
      },
    });

    gsap.to(headerSelectors.h2, {
      x: h2X,
      opacity: 1, // Fade in smoothly
      ease: 'power2.inOut', // Smooth easing
      zIndex: 2,
      scrollTrigger: {
        trigger: triggerElement,
        start,
        end,
        scrub: 2,
      },
    });

    gsap.to(headerSelectors.h3, {
      y: h3Y,
      opacity: 1, // Fade in smoothly
      ease: 'power2.inOut', // Smooth easing
      zIndex: 2,
      scrollTrigger: {
        trigger: triggerElement,
        start,
        end,
        scrub: 2,
      },
    });
  }
  /**
   * Hover Pulse: Applies a subtle pulse effect on hover for buttons or elements.
   */
  hoverPulse(
    element: string | HTMLElement,
    options: { duration?: number; scale?: number } = {},
  ): void {
    const { duration = 0.3, scale = 1.05 } = options;
    const el =
      typeof element === 'string' ? document.querySelector(element) : element;
    if (!el) return;

    el.addEventListener('mouseenter', () => {
      gsap.to(el, {
        scale,
        duration,
        ease: 'sine.inOut',
      });
    });

    el.addEventListener('mouseleave', () => {
      gsap.to(el, {
        scale: 1,
        duration,
        ease: 'sine.inOut',
      });
    });
  }

  /**
   * Card Tilt: Applies a 3D tilt effect on hover based on mouse position.
   */
  cardTilt(
    element: string | HTMLElement,
    options: { maxTilt?: number; duration?: number } = {},
  ): void {
    const { maxTilt = 10, duration = 0.3 } = options;
    const el =
      typeof element === 'string' ? document.querySelector(element) : element;
    if (!el) return;

    (el as HTMLElement).addEventListener('mousemove', (e: MouseEvent) => {
      const rect = el.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      const centerX = rect.width / 2;
      const centerY = rect.height / 2;
      const tiltX = ((y - centerY) / centerY) * maxTilt;
      const tiltY = -((x - centerX) / centerX) * maxTilt;

      gsap.to(el, {
        rotationX: tiltX,
        rotationY: tiltY,
        duration,
        ease: 'power2.out',
        transformPerspective: 1000,
      });
    });

    el.addEventListener('mouseleave', () => {
      gsap.to(el, {
        rotationX: 0,
        rotationY: 0,
        duration,
        ease: 'power2.out',
      });
    });
  }
  //Blur effect
  initBlurTransition(options: {
    panelsContainer: ElementRef;
    overlayId?: string;
    renderer: Renderer2;
  }): void {
    const {
      panelsContainer,
      overlayId = 'transition-overlay',
      renderer,
    } = options;
    const overlay = renderer.selectRootElement(`#${overlayId}`, true);

    const panels: NodeListOf<HTMLElement> =
      panelsContainer.nativeElement.querySelectorAll('.panel');

    if (panels.length > 0) {
      this.previousColor = panels[0].dataset['color'] || '#ffffff';
    }

    panels.forEach((panel: HTMLElement) => {
      ScrollTrigger.create({
        trigger: panel,
        start: 'top center',
        onEnter: () =>
          this.transitionTo(panel.dataset['color'] || '#ffffff', overlay),
        onEnterBack: () =>
          this.transitionTo(panel.dataset['color'] || '#ffffff', overlay),
      });
    });
  }

  private transitionTo(color: string, overlay: HTMLElement): void {
    const tl = gsap.timeline();

    tl.set(overlay, {
      background: this.previousColor,
      backdropFilter: 'blur(0px)',
      opacity: 0,
    })
      .to(overlay, {
        duration: 0.5,
        opacity: 1,
        backdropFilter: 'blur(20px)',
        ease: 'power2.out',
      })
      .to(overlay, {
        duration: 0.5,
        background: color,
        ease: 'none',
      })
      .to(overlay, {
        duration: 0.5,
        opacity: 0,
        backdropFilter: 'blur(0px)',
        ease: 'power2.in',
      });

    this.previousColor = color;
  }
}
