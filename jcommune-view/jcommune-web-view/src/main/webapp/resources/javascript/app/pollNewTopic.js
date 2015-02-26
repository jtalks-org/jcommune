$(document).ready(function() {

    if ($("div.pollItemsValue").length == maxPollItems) {
        $("a#add").hide();
    }

    $("#pollButton").click(function(){
        if($(this).attr("rel") == "add") {
            $(this).attr("value", $labelRemovePoll);
            $(this).attr("rel", "delete");
            $("div#editPollHolder").removeClass("hide");
        } else {
            $(this).attr("value", $labelAddPoll);
            $(this).attr("rel", "add");
            $("div#editPollHolder").addClass("hide");
            $("div#editPollHolder input[type=text]").val("");
            $("div#editPollHolder input[type=checkbox]").prop("checked", false);
        }
    });

    $("#sortable").sortable({update: function(event, ui) {reIndex()}});

    var name = $("#sortable").attr("rel");

    $("a#add").click(function(e){
        e.preventDefault();
        length = $("div.pollItemsValue").length;
        if(length < maxPollItems) {
            newItem = $(".pollItemsValue:last").clone();
            $(newItem).insertAfter(".pollItemsValue:last");
            $("input[type=text]", newItem).val("");
            reIndex();
            if ($("div.pollItemsValue").length == maxPollItems) {
                $("a#add").hide();
            }
        }
    });

    $(".pollItemsValue .remove").live("click", function(e){
        e.preventDefault();
        if($("div.pollItemsValue").length > minPollItems) {
            $(this).closest('div.pollItemsValue').remove();
            reIndex();
            $("a#add").show();
        }
    });

    function reIndex(){
        $(".pollItemsValue input[type=text]").each(function(index) {
            $(this).attr("name", name.replace("__index__", index));
        });
    }
});