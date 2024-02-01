let DIALOG_BG_EL = document.getElementById("DIALOG_BG");
let CREATE_NOTE_DIALOG_EL = document.getElementById("CREATE_NOTE_DIALOG");
let CREATE_NOTE_TEXT_EL = document.getElementById("CREATE_NOTE_TEXT");
let MAIN_EL = document.getElementById("MAIN");


let TOKEN = "4LfqJNO9w8x1x6rD";

let THIS_INSTANCE = {
	NAME:"る、みすきー",
	ICON:"https://rumiserver.com/Asset/MISSKEY/FAVICON.png",
	DOMAIN:"ussr.rumiserver.com",
	SOFTWARE_NAME:"rumisskey",
	THEME_COLOR:"#00FFB6"
};

window.addEventListener("load", async (E)=>{
	const RESULT = await GET_TIMELINE(CREATE_NOTE_TEXT_EL.value);

	if(RESULT !== false){
		for (let I = 0; I < RESULT.TIMELINE.length; I++) {
			const TIMELINE_DATA = RESULT.TIMELINE[I];
			ADD_NOTE_ELEMENT(MAIN_EL,
				{
					ID:TIMELINE_DATA.ID,
					TEXT:TIMELINE_DATA.TEXT,
					DATE:TIMELINE_DATA.DATE
				},
				{
					ID:TIMELINE_DATA.AUTHOR.ID,
					UID:TIMELINE_DATA.AUTHOR.UID,
					HOST:TIMELINE_DATA.AUTHOR.HOST,
					NAME:TIMELINE_DATA.AUTHOR.NAME,
					ICON:TIMELINE_DATA.AUTHOR.ICON
				},
				(function(){
					if(TIMELINE_DATA.INSTANCE){
						return {
							NAME:TIMELINE_DATA.INSTANCE.NAME,
							DOMAIN:TIMELINE_DATA.INSTANCE.DOMAIN,
							SOFTWARE_NAME:TIMELINE_DATA.INSTANCE.SOFTWARE_NAME,
							THEME_COLOR:TIMELINE_DATA.INSTANCE.THEME_COLOR,
							ICON:TIMELINE_DATA.INSTANCE.ICON
						};
					}else{
						return THIS_INSTANCE;
					}
				})()
			);
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

		//入力欄をリセット
		CREATE_NOTE_TEXT_EL.value = "";
	}
}

/**
 * 
 * @param {Element} EL 追加したい先
 * @param {Array} NOTE //ノート
 */
function ADD_NOTE_ELEMENT(EL, NOTE, AUTHOR, INSTANCE){
	let NOTE_EL = document.createElement("DIV");
	NOTE_EL.className = "NOTE";
	NOTE_EL.id = "NOTE-" + NOTE.ID;

	let USER_EL = document.createElement("DIV");
	USER_EL.className = "USER";
	USER_EL.innerHTML = `
							<IMG SRC="${AUTHOR.ICON}">
							<A HREF="/USER/${AUTHOR.UID}@${AUTHOR.DOMAIN}">${AUTHOR.NAME}</A>
							<SPAN CLASS="INSTANCE" STYLE="background-color: ${INSTANCE.THEME_COLOR};"><IMG SRC="${INSTANCE.ICON}">${INSTANCE.NAME}</SPAN>
						`;
	NOTE_EL.appendChild(USER_EL);

	let TEXT_EL = document.createElement("DIV");
	TEXT_EL.className = "TEXT";
	if(NOTE.TEXT){//本文があるなら追記
		TEXT_EL.innerHTML = NOTE.TEXT.replaceAll("\n", "<BR>");
	}
	NOTE_EL.appendChild(TEXT_EL);

	let STATUS_EL = document.createElement("DIV");
	STATUS_EL.className = "STATUS";
	NOTE_EL.appendChild(STATUS_EL);

	//作ったエレメントを指定のエレメントに追加
	EL.appendChild(NOTE_EL);
}