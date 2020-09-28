function readURL(input) {

        var reader = new FileReader();

        reader.onload = function(e) {

            $('.file-upload-image').attr('src', e.target.result);

        };

        reader.readAsDataURL(input.files[0]);

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