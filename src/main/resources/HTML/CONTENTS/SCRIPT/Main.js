let DIALOG_BG_EL = document.getElementById("DIALOG_BG");
let CREATE_NOTE_DIALOG_EL = document.getElementById("CREATE_NOTE_DIALOG");
let CREATE_NOTE_TEXT_EL = document.getElementById("CREATE_NOTE_TEXT");


let TOKEN = "4LfqJNO9w8x1x6rD";

function OPEN_DIALOG_BG(){
	if(DIALOG_BG_EL.style.display === "none"){
		DIALOG_BG_EL.style.display = "block";
	}else{
		DIALOG_BG_EL.style.display = "none";
	}
}

function OPEN_CREATE_NOTE_DIALOG(){
	OPEN_DIALOG_BG();
	if(CREATE_NOTE_DIALOG_EL.style.display === "none"){
		CREATE_NOTE_DIALOG_EL.style.display = "block";
	}else{
		CREATE_NOTE_DIALOG_EL.style.display = "none";
	}
}

function CREATE_NOTE_DIALOG_SUBMIT(){
	const RESULT = CREATE_NOTE(CREATE_NOTE_TEXT_EL.value);

	if(RESULT !== false){
		OPEN_CREATE_NOTE_DIALOG();
	}
}

async function CREATE_NOTE(TEXT){
	const AJAX = await fetch("/API/CREATE_NOTE", {
		method:"POST",
		headers:{
			TOKEN
		},
		body:JSON.stringify({
			TEXT
		})
	});

	if(AJAX.ok){
		return await AJAX.json();
	}else{
		return false;
	}
}