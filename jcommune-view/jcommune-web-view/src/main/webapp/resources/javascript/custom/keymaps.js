var Keymaps = {};

var escCode = 27;
var enterCode = 13;

Keymaps.review = function (e) {
    if (e.ctrlKey && e.keyCode == enterCode) {
        e.preventDefault();
        $(".review-container-controls-ok").click();
    }
    else if (e.keyCode == escCode) {
        e.preventDefault();
        $(".review-container-controls-cancel").click();
    }
};