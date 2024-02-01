let DIALOG_BG_EL = document.getElementById("DIALOG_BG");
let CREATE_NOTE_DIALOG_EL = document.getElementById("CREATE_NOTE_DIALOG");
let CREATE_NOTE_TEXT_EL = document.getElementById("CREATE_NOTE_TEXT");
let MAIN_EL = document.getElementById("MAIN");


let TOKEN = "4LfqJNO9w8x1x6rD";

window.addEventListener("load", async (E)=>{
	const RESULT = await GET_TIMELINE(CREATE_NOTE_TEXT_EL.value);

	if(RESULT !== false){
		for (let I = 0; I < RESULT.TIMELINE.length; I++) {
			const TIMELINE_DATA = RESULT.TIMELINE[I];
			ADD_NOTE_ELEMENT(MAIN_EL, TIMELINE_DATA.ID, TIMELINE_DATA.TEXT);
		}
	}
});

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

async function CREATE_NOTE_DIALOG_SUBMIT(){
	const RESULT = await CREATE_NOTE(CREATE_NOTE_TEXT_EL.value);

	if(RESULT !== false){
		OPEN_CREATE_NOTE_DIALOG();

		ADD_NOTE_ELEMENT(MAIN_EL, RESULT.ID, CREATE_NOTE_TEXT_EL.value);

		//入力欄をリセット
		CREATE_NOTE_TEXT_EL.value = "";
	}
}

function ADD_NOTE_ELEMENT(EL, NOTE_ID, NOTE_TEXT){
	let NOTE_EL = document.createElement("DIV");
	NOTE_EL.className = "NOTE";
	NOTE_EL.id = "NOTE-" + NOTE_ID;

	let TEXT_EL = document.createElement("DIV");
	TEXT_EL.innerHTML = NOTE_TEXT.replaceAll("\n", "<BR>");
	NOTE_EL.appendChild(TEXT_EL);

	//作ったエレメントを指定のエレメントに追加
	EL.appendChild(NOTE_EL);
}