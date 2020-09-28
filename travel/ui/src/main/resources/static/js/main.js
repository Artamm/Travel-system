window.onscroll = function () {
    scrollFunction()
};

function scrollFunction() {

    if (document.body.scrollTop > 30 || document.documentElement.scrollTop > 30) {
        document.getElementById("top").style.display = "block";

    } else {

        document.getElementById("top").style.display = "none";

    }

}

function topFunction() {

    document.body.scrollTop = 0; // For Safari
    document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera

}

function checkPassword() {
     var pas1 = document.getElementById("pas1").value;
     var pas2 = document.getElementById("pas2").value;

     if (pas1 !== pas2) {
         document.getElementById("inform").style.display = "block";
         document.getElementById("changePas").disabled = true;
     }
     if(pas1===pas2){
         document.getElementById("inform").style.display = "none";
         document.getElementById("changePas").disabled = false;
     }
     if (pas1===""||pas2===""||pas2===null||pas1===null){
         document.getElementById("changePas").disabled = true;
     }

}
