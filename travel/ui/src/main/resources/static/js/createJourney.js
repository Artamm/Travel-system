var index = 0;

function AddLocation() {

}

function AddPerson() {

    // var fieldHTML = '<div><input type="text" name="field_name[]" th:field="*{people}" /></div>';
    // var addButton = $('.add_button');
    // var wrapper = $('.invitedList');
    // //Once add button is clicked
    // $(addButton).click(function(){
    //         $(wrapper).append(fieldHTML); //Add field html
    //
    // });


    $(
        '<input type="text" class="form-control quantity" field="people[' + index + '].username}">'
    ).appendTo('#invitedList');
    index++;
}
//
function BlockChoice(id) {
    var openChoice = document.getElementById("peopleNumber");
//     var inviteChoice = document.getElementById("invited");
    var checkOpen = document.getElementById("checkOpened");
    var checkInvited = document.getElementById("checkInvited");
var openChoiceFields=document.getElementById("choice-open");
    var invitedChoiceFields=document.getElementById("invited-field");
    var invitedDiv = document.getElementById("choice-invitation");


    var open= document.getElementById("open");
    var invitation= document.getElementById("invitation");

    if(document.getElementById(id)===checkOpen){

        checkInvited.checked=false;
        openChoice.disabled = false;
        document.getElementById("addButton").disabled = true;
        openChoiceFields.style.display="block";
        invitedChoiceFields.style.display="none";
        invitedDiv.style.display="none";
        open.style.display="block";
        invitation.style.display="none";

    }
    else{
        checkOpen.checked=false;
        openChoice.disabled = true;
        document.getElementById("addButton").disabled = false;
        openChoiceFields.style.display="none";
        invitedChoiceFields.style.display="block";
        invitedDiv.style.display="block";
        open.style.display="none";
        invitation.style.display="block";


    }

}

function Estimatedtime() {
    var start = document.getElementById("dateStart").getTime();
    var end = document.getElementById("dataEnd").getTime();

}

function LoneWolf() {
    var peopleNumber = document.getElementById("peopleNumber").value;

    if (peopleNumber === "1") {
        document.getElementById("loneWolf").style.display = "block";
    } else {
        document.getElementById("loneWolf").style.display = "none";
    }

}
function  checkDate() {

    var date1 = new Date(document.getElementById("start_date").value);
    var date2 = new Date(document.getElementById("end_date").value);

    var difference = date2 - date1;

    if (difference < 0)
        document.getElementById("wrong_dates").style.display="block";
    else{
        document.getElementById("wrong_dates").style.display="none";

    }
}



