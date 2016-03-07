$(document).ready(function()    {
	var paginationOptions = {
		    innerWindow: 1
		  };
	
	var datasetList = new List('test-list', {
		  valueNames: ['dataTitle'],
		  page: 5,
		  innerWindow: 1,
		  plugins: [ ListPagination(paginationOptions) ] 
		});
});
