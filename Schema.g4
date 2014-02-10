grammar Schema;

schema :
      ( command '\n' )* ;

command :
    letblock
  | clarifyblock
  | loadcmd
  | querycmd
  ;

querycmd: GET ID COLON QSTRING (PERIOD|SEMI)			#Query
     ;

queryfile : (queryline)*
	  ;

queryline : constraints (SEMI|PERIOD) '\n'		#GetQuery
	  ;


constraints : constraint				#GetFirstConstraint
	    | constraints COMMA constraint		#GetNextConstraint
	    ;

constraint : ID comp value				#EvalConstraint
      ;

comp : EQUALS						#EqualTo
     | NEQUALS						#NotEqualTo
     | LESS						#LessThan
     | MORE						#MoreThan
     | LEAST						#AtLeast
     | MOST						#AtMost
     ;

value : v=NUMBER					#EvalNumberValue
      | v=(TRUE|FALSE)					#EvalBoolValue
      | v=QSTRING					#EvalQStringValue
      ;

clarifyblock :
    CLARIFY ptype AS ID (PERIOD|SEMI)           #ClarifyType
  ;

letblock :
    LET ID COLON proplist (PERIOD|SEMI)		#LetType
    ;

loadcmd :
    LOAD ID COLON QSTRING (SEMI|PERIOD)		#Load
    ;

proplist :
    prop					#AddFirstProp
  | proplist COMMA prop				#AddNextProp
  ;

prop :
    ptype ID					#DeclareProp
  ; 

ptype :	
    t=INT					# Getsym
  | t=BOOL					# Getsym
  | t=STR					# Getsym
  | t=ID					# Getsym
  ;

csvfile : (csvline)*
	;

csvline : csvrecord '\n' 			# GetCSVRecord
	;

csvrecord : csventry				# GetFirstCSVEntry
          | csvrecord COMMA csventry		# GetNextCSVEntry
	  ;

csventry : v=NUMBER				# GetCSVNumber
	 | v=(TRUE|FALSE)			# GetCSVBool
	 | v=QSTRING				# GetCSVQstr
	 ;

fragment QUOTE : '"';
fragment ESC_QUOTE : '\\"' ;
fragment ESC_BSLASH : '\\\\' ;
QSTRING : QUOTE ( ~('"'|'\\')+ | ESC_QUOTE | ESC_BSLASH )* QUOTE ;

TRUE    : 'true' ;
FALSE   : 'false' ;

NUMBER : DIGIT+ ;
fragment DIGIT : [0-9] ;

GET     : 'get' ;
AS	: 'as' ;
INT	: 'int' ;
BOOL	: 'bool' ;
STR	: 'string' ;
LET	: 'let' ;
LOAD	: 'load' ;
CLARIFY	: 'clarify' ;
COLON	: ':' ;
SEMI	: ';' ;
PERIOD	: '.' ;
COMMA	: ',' ;
NEQUALS  : '!=' ;
EQUALS  : '=' ;
LEAST   : '>>' ;
MOST    : '<<' ;
LESS    : '<' ;
MORE    : '>' ;

ID : [A-Za-z] [A-Za-z0-9]* ;
WS : [ \t\n\r]+ -> skip ;
