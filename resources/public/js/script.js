$(document).ready(function() {
    console.log("the document is ready");

    // init
    const SWIMLANES = ["Backlog", "InProgress", "Complete"]


    function hideThings() {
        // hide arrows that shouldn't be displayed.
        var allLeftButtons = $(".ticket-navigation-left");
        var allRightButtons = $(".ticket-navigation-right");

        allLeftButtons.css("visibility", "visible");
        allRightButtons.css("visibility", "visible");

        var hiddenRightButtons = $("#Complete").find(allRightButtons)
        hiddenRightButtons.css("visibility", "hidden");

        var hiddenLeftButtons = $("#Backlog").find(allLeftButtons);
        hiddenLeftButtons.css("visibility", "hidden");

        // TODO: hide the add card form.
    }

    // Setup
    hideThings();

    // Click events
    $("#add-card").click(function() {
        console.log("add button clicked");
    });
    $(".ticket-navigation-right, .ticket-navigation-left").click(function() {
        var card = $(this).parent();
        var currentSwimlane = card.attr("swimlane");
        var swimlaneIndex = SWIMLANES.indexOf(currentSwimlane);
        var moveRight = $(this).attr("class") == "ticket-navigation-right";
        var newSwimlaneName = moveRight ? SWIMLANES[swimlaneIndex + 1] : SWIMLANES[swimlaneIndex - 1];
        var newSwimlane = $("#" + newSwimlaneName + ">.swimlane-body>.tickets-container");

        newSwimlane.append(card);
        card.attr("swimlane", newSwimlaneName);
        hideThings();
    });
});
