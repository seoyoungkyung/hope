document.addEventListener('DOMContentLoaded', function () {
    var swiper1 = new Swiper('.swiper-container1', {
        slidesPerView: 1,
        spaceBetween: 10,
        loop: true,
        autoplay: {
            delay: 5000,
            disableOnInteraction: false
        },
        navigation: {
            nextEl: '.swiper-button-next1',
            prevEl: '.swiper-button-prev1'
        },
        pagination: {
            el: '.swiper-pagination', // 페이지네이션 요소
            clickable: true // 클릭 가능하게 설정
        },
    });

    // 각 swiper-container-custom에 대해 고유하게 스와이퍼 초기화
    document.querySelectorAll('.swiper-container-custom').forEach(function(container, index) {
        var swiper = new Swiper(container, {
            slidesPerView: 4,
            loop: true,
            autoplay: {
            delay: 4000,
            disableOnInteraction: false
            },
        });
    });
});