
//the id of the form element containing the wizard plugin
var wizardForm;

/***
 * Function that initializes the html components of the /petitionregistration/view.jsp
 */
function init_uploader(){
	
	//********************** Variables **********************
		
	   		
	//********************** Action **********************
    			
	//get the next and previous steps localized labels
	var nextMsg = Liferay.Language.get('next-step');//"<liferay-ui:message key='next-step' />";
	var prevMsg = Liferay.Language.get('previous-step');//"<liferay-ui:message key='previous-step' />";
	
	//**************************** init bs wizard ******************************
     /* $(".bs-wizard").bs_wizard({
    	  "nextText": ""+nextMsg,
    	  "backText": ""+prevMsg,
    	  "beforeNext": validateStep
      });*/
      
      wizardForm = "uploader_form";
      
      //*********************** initialize all selects *************************
 
      $('#license').select2(
    	{
	  		minimumResultsForSearch: Infinity,
	  		
    	}	  
      );
      
      $('#format').select2(
    	    	{
    		  		minimumResultsForSearch: Infinity,
    		  		
    	    	}	  
    	      );
      
      
      //*************** initialize file input ************************
      //document blob file input
      $("#documentInput").fileinput(
    		   {
     		  	    allowedFileTypes: ['csv','xml','json','geojson','xls','xlsx'],
      			}
    		   
    		  );      
      
}//end init_petition_registration()

/***
 * Function that retrieves the current step of the bs wizard
 * @returns : the current step
 */
function current_step() {
	 return $(".bs-wizard").bs_wizard('option', 'currentStep');
}

/**
 * Function that validates the fields of the current step of the wizard  
 */	
function validateStep(){
					
	var valid = validateFields($('.step_'+current_step()), current_step());
	
	//if we have passed validation and we are at step 1 (sender data), 
	//check if there is a document already registered with these data
	if(valid === true && current_step() == 1){
		//checkBySenderData();
	}
	
	return valid === true;
						
}//end validateStep()
	
/***
 * It validates the fields of the wizard form at a certain step
 * @param fields : An array of the fields to be validated
 * @param step : The current step of the wizard
 * @returns : True if validation passes and error if not
 */
function validateFields(fields, step) {
	
    var error_step, field, _i, _len;
    		    
    for (_i = 0, _len = fields.length; _i < _len; _i++) {
      
    	field = fields[_i];
      		    			    	
      if (!wizardValidator().element(field)) {
        error_step = step;
      }
    }
    return error_step != null ? error_step : true;
 
}//end validate_fields()

/***
 * It initializes the jquery validator for the wizard form
 * @returns: A jquery validator object
 */
function wizardValidator() {
	
    return $('#'+wizardForm).validate({
    	
    	errorPlacement: function(error, element) {
            error.appendTo( element.parent() );
        },
		highlight: function( element, errorClass, validClass ) {
												
			if ( element.type === "radio" ) {
				this.findByName( element.name ).addClass( errorClass ).removeClass( validClass );
			}
			else {
					$( element ).parent().addClass( errorClass ).removeClass( validClass );
			}
		},
		unhighlight: function( element, errorClass, validClass ) {
										
			if ( element.type === "radio" ) {
						
				this.findByName( element.name ).removeClass( errorClass ).addClass( validClass );
						
			} 
			else {
				$( element ).parent().removeClass( errorClass ).addClass( validClass );
			}
		}
		    
    });
    
}//end wizard_validator()

