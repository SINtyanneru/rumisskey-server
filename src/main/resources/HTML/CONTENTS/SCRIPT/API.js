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

async function GET_TIMELINE(){
	const AJAX = await fetch("/API/GET_TIMELINE", {
		method:"GET",
		headers:{
			TOKEN
		}
	});

	if(AJAX.ok){
		return await AJAX.json();
	}else{
		return false;
	}
}
