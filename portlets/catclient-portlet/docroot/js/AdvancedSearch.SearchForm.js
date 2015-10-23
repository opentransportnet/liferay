AdvancedSearch = {};

AdvancedSearch.SearchForm = function(config, resultContainer){

    this.handler = config.handler;
    this.catClient = config.catClient;
    this.addField = config.addField;
    this.srvKeywordsPath = config.cfg.srvKeywordsPath;
    this.resultContainer=resultContainer;

    if(config.cfg.thesaurusPath) this.thesPath = config.cfg.thesaurusPath;
    else this.thesPath = 'libs/gemetclient/';

    var _thesaurusWindow = null;

    //--- seznam organizaci do combo boxu
    var partyStore = new Ext.data.JsonStore({
        // TODO - do konfigurace
        url: config.cfg.mickaPath+'/util/organizace.php',
        root: 'records',
        id: 'id',
        baseParams: {lang: HS.getLang(), mdcontact: 1},
        fields: ['id', 'name']
    });

    // meritka
    var scaleList = new Ext.data.SimpleStore({
        id: 0,
        fields: ['label'],
        data : [[1000],[5000],[10000],[50000],[100000],[500000],[1000000]]
    });

    // zda data nebo sluzba
    var typeList = new Ext.data.SimpleStore({
        data: [['',' '],['data',HS.i18n('data')],['service',HS.i18n('service')]],
        fields: ['name', 'label']
    });

    var orderList = new Ext.data.SimpleStore({
        data: [['title', HS.i18n('Title')], ['date', HS.i18n('Metadata Date')]],
        fields: ['kod', 'label']
    });

    // --- XML cteni ---
    var xmlRecordDef = Ext.data.Record.create([
        {name: 'name', mapping: '@name'},
        {name: 'label', mapping: '/'}
    ]);

    var xmlRecordDefCode = Ext.data.Record.create([
        {name: 'name', mapping: '@code'},
        {name: 'label', mapping: '/'}
    ]);

    var topicsReader = new Ext.data.XmlReader({
        record: "topicCategory/value"
    }, xmlRecordDef);

    var topicsStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: topicsReader,
        autoLoad: true
    });

    var inspireReader = new Ext.data.XmlReader({
        record: "inspireKeywords/value"
    }, xmlRecordDefCode);

    var inspireStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: inspireReader,
        autoLoad: true
    });

    var langReader = new Ext.data.XmlReader({
        record: "language/value"
    }, xmlRecordDefCode);

    var langStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: langReader,
        autoLoad: true
    });

    var serviceTypeReader = new Ext.data.XmlReader({
        record: "serviceType/value"
    }, xmlRecordDef);

    var serviceStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: serviceTypeReader,
        autoLoad: true
    });

    var serviceClassReader = new Ext.data.XmlReader({
        record: "serviceKeyword/value"
    }, xmlRecordDef);

    var serviceClassStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: serviceClassReader,
        autoLoad: true
    });

    var roleReader = new Ext.data.XmlReader({
        record: "specRole/value"
    }, xmlRecordDef);

    var roleStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: roleReader,
        autoLoad: true
    });

    var dateTypeReader = new Ext.data.XmlReader({
        record: "dateType/value"
    }, xmlRecordDef);

    var dateTypeStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: dateTypeReader,
        autoLoad: true
    });

    var aConstrReader = new Ext.data.XmlReader({
        record: "accessConstraints/value"
    }, xmlRecordDef);

    var aConstrStore = new Ext.data.Store({
        url: config.cfg.xslPath + '/codelists_'+HS.getLang()+'.xml',
        reader: aConstrReader,
        autoLoad: true
    });


    var r = new langStore.recordType({name:'hun', label:'Hunstina'},-1);
    langStore.insert(0, r);

    var kw = new Ext.form.TriggerField({
        fieldLabel: HS.i18n('Keywords'),
        width: 300,
        emptyText: HS.i18n("Free keyword or select from thesaurus"),
        name:'keywords'
    });

    kw.onTriggerClick = function(a,b,c){
        if(!_thesaurusWindow){
            _thesaurusWindow = new Ext.Window({
                width:400,
                height:500,
                layout: 'fit',
                closeAction:'hide',
                items: keywordManager
            });
        }
        _thesaurusWindow.show();
    };

    this.getValues= function(){
        var form = this.theForm.getForm();
        var fields = this.theForm.getForm().items;
        var removeEmptyText = function(f){
            if(f.emptyText == vals[f.getName()]){
                vals[f.getName()] = "";
            }
        }
        var vals = form.getValues();
        fields.each(removeEmptyText);
        return vals;
    }

    // Nastavi obsah formulare (napr. daty zvenku)
    this.setValues= function(v){
        if (v.hlevel && v.hlevel.length>0) {
            var buttons = this.theForm.items.items[0].items.items;
            for(var i=0;i<buttons.length; i++){
                if(buttons[i].fieldLabel == v.hlevel) buttons[i].toggle();
            }
        };
        this.theForm.form.setValues(v);

        if (v.ttype && v.ttype.length>0) {
            var radios = this.theForm.items.items[3].items.items;
            for(var i=0; i<radios.length; i++) {
                if(radios[i].inputValue == v.ttype) radios[i].setValue(true);
            }
        } else {
            this.theForm.items.items[3].items.items[0].setValue(true);
        }

        this.onChangeResource();
    }

    this.onChangeResource = function(combo, record, index){
    var values = this.getValues();
    var s = this.comboServiceType.getEl();
    var s = s.findParent('div').parentNode.parentNode;
    s.style.display = 'none';

    var d = this.comboTopicCategory.getEl();
    var d = d.findParent('div').parentNode.parentNode;
    d.style.display = 'none';
        if(values.hlevel=='service'){
            s.style.display = 'block';
            sc.style.display = 'block';
        }
        else if(values.hlevel=='data') d.style.display = 'block';
    }

    this.onEnter = function(f,e){
        if(e.getKey() == e.ENTER){
            this.search(true);
        }
    }

    this.fillKeywords = function(result){
        this.theForm.form.setValues({'keywords': result.terms[HS.getLang(2)]});
        _thesaurusWindow.hide();
    };

    var thesaurusReader = new ThesaurusReader({
        lang: HS.getLang(2),
        outputLangs: [HS.getLang(2)],
        separator: '/',
        appPath: this.thesPath,
        returnPath: false,
        returnInspire: true,
        handler: this.fillKeywords,
        scope: this,
        defaultThesaurus: 'GEMET',
        thesaurus: {
            'GEMET': {},
            '1GE': {
                url: "http://gemet.esdi-humboldt.cz/thesaurus/",
                concept: "http://www.onegeology-europe.eu/concept/",
                theme: "http://www.onegeology-europe.eu/theme/",
                group: null,
                supergroup: null
            }
        }
    });


    var keywordManager = new Ext.Panel({
        id:'gemet', title: 'Thesaurus', items: [thesaurusReader], layout: 'fit'
    });


    this.boundingBoxField = new Ext.form.TextField({
        name: 'bbox',
        width: 200,
        emptyText: HS.i18n("Take from map"),
        fieldLabel: HS.i18n('Bounding box')
    });

    this.setTypes = function(obj, resetButtons){
        var items = obj.ownerCt.items.items;
        if(!resetButtons) obj = null;
        for(var i=0; i<items.length; i++){
            if(items[i] != obj) {
                items[i].toggle(false)
            } else {
                var menuNo = i+1;
            };
        }

        if(obj && obj.pressed) {
            this.theForm.getForm().setValues({hlevel: obj.fieldLabel});
        } else {
            this.theForm.getForm().setValues({hlevel: ''});
        }

        this.onChangeResource();
    };

    this.comboServiceType = new Ext.form.ComboBox({
        xtype: 'combo',
        fieldLabel: HS.i18n('Service Type'),
        //id: 'service-select',
        hideParent: true,
        displayField:'label',
        valueField: 'name',
        hiddenName: 'serviceType',
        typeAhead: true,
        store: serviceStore,
        triggerAction: 'all',
        editable: false,
        mode:'local'
    });

    this.comboTopicCategory = new Ext.form.ComboBox({
        xtype: 'combo',
        width: 300,
        fieldLabel:HS.i18n('Topic category'),
        //id: 'topic-select',
        displayField:'label',
        valueField: 'name',
        forceSelection: true,
        hiddenName: 'topic',
        typeAhead: true,
        store: topicsStore,
        editable: false,
        triggerAction: 'all',
        mode:'local'
    });

    this.theForm = new Ext.FormPanel({
        frame:false,
        border:false,
        labelWidth: 120,
        //layout: 'fit',
        buttonAlign: 'center',
        //defaults: {width: 300},
        defaultType: 'textfield',
        autoScroll: true,
        items:[{
            fieldLabel: HS.i18n('Resource Type'),
            xtype: 'panel',
            layout: 'table',
            defaultType: 'button',
            defaults: {width:80, ennableToggle: true},
            items: [{
                fieldLabel: 'data',
                enableToggle: true,
                scale: 'medium',
                iconCls: 'cat-imgsprite iconData',
                listeners: {'click': this.setTypes, scope: this},
                text: HS.i18n('Data')
            },{
                fieldLabel: 'service',
                enableToggle: true,
                scale: 'medium',
                iconCls: 'cat-imgsprite iconService',
                listeners: {'click': this.setTypes, scope: this},
                text: HS.i18n('Service')
            },{
                fieldLabel: 'map',
                enableToggle: true,
                scale: 'medium',
                iconCls: 'cat-imgsprite iconMap',
                listeners: {'click': this.setTypes, scope: this},
                text: HS.i18n('Map')
            }]
        },{
            name: 'hlevel',
            xtype: 'hidden'
        },{
            name: 'anytext',
            width: 300,
            selectOnFocus:true,
            fieldLabel: HS.i18n('Free text'),
            listeners: {specialkey: this.onEnter, scope: this},
            minChars: 3
        },{
            xtype: 'radiogroup',
            fieldLabel: HS.i18n('Free text scope'),
            autoHeight: true,
            items: [
                {boxLabel: HS.i18n('Fulltext'), name: 'ttype', inputValue: 'Anytext', checked: true},
                {boxLabel: HS.i18n('Title'), name: 'ttype', inputValue: 'title'},
                {boxLabel: HS.i18n('Abstract'), name: 'ttype', inputValue: 'mdAbstract'},
                {boxLabel: HS.i18n('Lineage'), name: 'ttype', inputValue: 'lineage'}
            ]
        },{
            name: 'party',
            xtype: 'combo',
            width: 300,
            fieldLabel: HS.i18n('Organisation'),
            store: partyStore,
            displayField:'name',
            valueField:'name',
            typeAhead: true,
            triggerAction: 'all',
            minChars: 3
        },
        this.comboServiceType,
        this.comboTopicCategory,
        kw,

//--- bounding box
        {
            xtype: 'panel',
            layout: 'column',
            items:[{
                width: 330,
                layout: 'form',
                items:[this.boundingBoxField]
            },{
                labelWidth: 1,
                layout: 'form',
                items:[{
                    xtype: 'checkbox',
                    boxLabel: HS.i18n('Inside only'),
                    name: 'inside'
                }]
            }]
        },{
            xtype: 'panel',
            layout: 'column',
            width: '100%',
            items:[{
                width: 220,
                layout: 'form',
                items:[{
                    xtype: 'datefield',
                    format: 'd.m.Y',
                    fieldLabel: HS.i18n('Date'),
                    name: 'dateFrom',
                    emptyText: HS.i18n("from"),
                    width: 90
                }]
        },{
            labelWidth: 1,
            width: 100,
            layout: 'form',
            items:[{
                xtype: 'datefield',
                format: 'd.m.Y',
                name: 'dateTo',
                emptyText: HS.i18n("to"),
                width: 90
            }]
        },{
            labelWidth: 30,
            layout: 'form',
            items:[{
                fieldLabel: HS.i18n('Type'),
                xtype: 'combo',
                width: 80,
                displayField:'label',
                valueField: 'name',
                store: dateTypeStore,
                mode: 'local',
                triggerAction: 'all',
                editable: false,
                value: "revision",
                hiddenName: 'dateType'
                }]
        }]
    },{
        xtype: 'combo',
        hiddenName: 'sortBy',
        width: 150,
        store: orderList,
        fieldLabel: HS.i18n('Order by'),
        mode:'local',
        valueField: 'kod',
        value: 'title',
        displayField:'label',
        editable: false,
        forceSelection: true,
        typeAhead: true,
        triggerAction: 'all',
        valueField: 'kod'
    }]
});

this.clearForm = function(){
    this.theForm.getForm().reset();
    var resourceTypeItems = this.theForm.items.items[0].items.items;
    this.setTypes(this.theForm.items.items[0].items.items[1], false);
    this.fireEvent("formcleared",this.theForm);
    if(this.addField) this.addField.value = '';
}

/**
 * Process form values to search handler
 * @param activate {Boolean} to activate TabPanel with CSW
 */
    this.search = function(activate){
        var formValues = this.getValues();
        this.handler(formValues, {sortBy:formValues.sortBy,activate: activate});
    }

    config.bbar =  {
        buttonAlign: 'center',
        items: [{
            xtype:'button',
            iconCls:'cat-imgsprite cat-iconSearch',
            text: HS.i18n('Search'),
            scale: 'medium',
            scope: this,
            handler: function(){this.search(true);}
        },'-',{
            text: HS.i18n('Clear'),
            iconCls:'cat-imgsprite cat-iconClear',
            scale: 'medium',
            scope: this,
            handler:  function(){
                this.clearForm();
            }
        }]
    };

    config.items=[this.theForm];

    AdvancedSearch.SearchForm.superclass.constructor.call(this,config);

    this.addEvents("formcleared");

};

Ext.extend(AdvancedSearch.SearchForm, Ext.Panel, {
    boundingBoxField: undefined,
    theForm: undefined
});

AdvancedSearch.SearchForm.date2iso  = function (d){
    if(d.indexOf('.')>-1){
        var s = d.split('.');
        if(s[2]) d = s[2]+'-'+s[1]+'-'+s[0];
        else d = s[1]+'-'+s[0];
    }
    return d;
};

