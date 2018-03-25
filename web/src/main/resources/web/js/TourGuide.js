function TourGuide() {


    var _this = this;

    function initTour() {
        // Instance the tour

        var tourSteps = [
            {
                element: "#server-url",
                title: "STEP 1: Server",
                content: "Enter Your Server Url. Normally the same url you enter in ODK"
            }, {
                element: "#username",
                title: "STEP 2: Username",
                content: "Enter Server User Name"
            },
            {
                element: "#password",
                title: "STEP 3: Password",
                content: "Enter Server Password"
            },
            {
                element: "#get-form-list",
                title: "STEP 4: Forms",
                content: "Retrieve Form List"
            },
            {
                element: "#c-form-list",
                title: "STEP 5: Forms",
                content: "Select One Form"
            },
            {
                element: "#c-numOfEntries",
                title: "STEP 6: Entries",
                content: "Enter The Number Of Fake Data Entries You Wish To Generate"
            },
            {
                element: "#c-dry-run",
                title: "STEP 7: Test Drive",
                content: "Uncheck this to actually submit the generated data to the server"
            },
            {
                element: "#c-generexExpressions",
                title: "STEP 8: Generator/Calculate Expressions",
                content: "Leave blank or enter Some calculate functions/formulas to influence the data generated. All javarosa functions are supported plus the ones on the right."
                + " <i class='glyphicon glyphicon-hand-right'></i>"
                + "<p>E.g</p>"
                + "<p><code>fake('superhero','name')</code>  to generate a random super hero name </p>"
                + "<p><code>random-number(10)</code> Produces a value less than 10</p>"
                + "<p><code>random-past-date(5,'days')</code> Produces a random date not exceeding 5 days ago</p>"

            },
            {
                element: "#c-btn-generate-data",
                title: "STEP 9: Data Generation",
                content: "Click to start the data generation process"
            }
        ];
        _this.tour = new Tour({steps: tourSteps});
    }

    initTour();


}

TourGuide.prototype = {

    prototype: TourGuide,

    start: function () {
        this.tour.start();
    },

    restart: function () {
        this.tour.restart();
    }
};