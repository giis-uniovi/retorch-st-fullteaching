// Hide Header on on scroll down
let didScroll;
let lastScrollTop = 0;
const deltaDown = 1;
const deltaUp = 1;
const navbarHeight = $('#navigation-bar').outerHeight();
let smallDevice = $(globalThis).width() <= 600 || $(globalThis).height() <= 600;

$(globalThis).resize(function(event){
  smallDevice = $(globalThis).width() <= 600 || $(globalThis).height() <= 600;
});

$(globalThis).scroll(function(event) {
  didScroll = !smallDevice;
});

setInterval(function() {
  if (didScroll) {
    hasScrolled();
    didScroll = false;
  }
}, 200);

function hasScrolled() {
  const st = $(this).scrollTop();

  // If they scrolled down and are past the navbar, add class .nav-up.
  // This is necessary so you never see what is "behind" the navbar.
  if (st > lastScrollTop && st > navbarHeight) {
    // Make sure they scroll more than delta
    if (Math.abs(lastScrollTop - st) <= deltaDown)
      return;
    // Scroll Down
    $('#navigation-bar').addClass('nav-up');
    $('.dropdown-button').dropdown('close');
  } else {
    // Make sure they scroll more than delta
    if(Math.abs(lastScrollTop - st) <= deltaUp)
        return;
    // Scroll Up
    if (st + $(globalThis).height() < $(document).height()) {
      $('#navigation-bar').removeClass('nav-up');
    }
  }
  lastScrollTop = st;
}
