
/*
 * CS-252
 * shell.y: parser for shell
 *
 * This parser compiles the following grammar:
 *
 *	cmd [arg]* [> filename]
 *
 * you must extend it to understand the complete shell grammar
 **
 */

%token	<string_val> WORD

%token 	NOTOKEN GREAT NEWLINE GREATGREAT PIPE AMPERSAND GREATAMPERSAND GREATGREATAMPERSAND LESS

%union	{
		char   *string_val;
	}

%{
//#define yylex yylex
#define MAXFILENAME 1024

#include <assert.h>
#include <regex.h>
#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "command.h"

void yyerror(const char * s);
int yylex();

int maxEntries;
int nEntries;
char ** array;

int cmpfunc (const void * a, const void * b) {
	const char **ia = (const char **)a;
	const char **ib = (const char **)b;
	return strcmp(*ia, *ib);
}

void expandWildcards(/* char * arg */char * prefix, char * suffix) {
	if (suffix[0] == 0) {
		//suffix is empty so put prefix in argument
		//Command::_currentSimpleCommand->insertArgument(strdup(prefix));
		if (nEntries == maxEntries - 1) {
			maxEntries *= 2;
			array = (char **)realloc(array, sizeof(char *) * maxEntries);
		}
		array[nEntries] = strdup(prefix);
		nEntries++;
		return;
	}
	//Obtain next component in suffix and advance it
	//need to handle when first char is /
	char * s = strchr(suffix, '/');
	char component[MAXFILENAME] = "";
	if (s != NULL) {
		if(s - suffix != 0) { //equals zero when starts with /
			strncpy(component, suffix, s - suffix);
		}
		//starts with /
		else { 
			//size is zero in strncpy so component null as root flag
			component[0] = '\0';
		}
		//advance the suffix to rid /
		suffix = s + 1;
	}
	else { //Last part of path so copy entirety
		strcpy(component, suffix);
		suffix = suffix + strlen(suffix);
	}
	
	//Expand component
	char newPrefix[MAXFILENAME];
	// Return if arg does not contain '*' or '?'
	if (strchr(component ,'*') == NULL && strchr(component ,'?') == NULL) {
		//Command::_currentSimpleCommand->insertArgument(arg);
		if (prefix == NULL && component[0] != '\0') {
			sprintf(newPrefix, "%s", component);
		}
		else {
			sprintf(newPrefix, "%s/%s", prefix, component);
		}
		//handle empty component
		if (component[0] == '\0') {
			expandWildcards((char*)"", suffix);
		}
		else {
			expandWildcards(newPrefix, suffix);
		}
		return;
	}

	//convert the wildcards to regex
	char * reg = (char*)malloc(2*strlen(component)+10);
	char * a = component;
	char * r = reg;
	*r = '^'; r++; //match beginning
	while (*a) {
	    if (*a == '*') { *r='.'; r++; *r='*'; r++; }
	    else if (*a == '?') { *r='.'; r++;}
	    else if (*a == '.') { *r='\\'; r++; *r='.'; r++;}
		else if (*a == '/') {} //don't want slashes when expanding with regex
	    else { *r=*a; r++;}
	    a++;
	}
    *r='$';
	r++; *r=0;

	regex_t re;
	/*char * expbuf = regcomp(&re, reg, REG_EXTENDED );*/
	int result = regcomp(&re, reg, REG_EXTENDED|REG_NOSUB);
	if (result != 0) {
	    perror("regcomp");
	    return;
	}

	//considering when the current directory
	const char * currDir;
	if (prefix == NULL) {
		currDir = ".\0";
	}
	//considering when starts with root
	else if (!strcmp(prefix, "")) {
		currDir = "/\0";
	}
	else {
		currDir = prefix;
	}

	DIR * dir = opendir(currDir);
	if (dir == NULL) {
	    //perror("opendir");
	    return;
	}

	struct dirent * ent;
	while ( (ent = readdir(dir)) != NULL) {
		//check if name exists
		//regmatch_t match;
		result = regexec(&re , ent->d_name, 0, NULL, 0);
	    if (result == 0) {
			if(ent->d_name[0] == '.') {
				if (component[0] == '.') {
					if(prefix == NULL) {
						sprintf(newPrefix, "%s", ent->d_name);
					}
					else {
						sprintf(newPrefix, "%s/%s", prefix, ent->d_name);
					}
					expandWildcards(newPrefix, suffix);
				}
			}
			else {
				if(component[0] != '.') {
					if(prefix == NULL) {
						sprintf(newPrefix, "%s", ent->d_name);
					}
					else {
						sprintf(newPrefix, "%s/%s", prefix, ent->d_name);
					}
					expandWildcards(newPrefix, suffix);
				}
			//	array[nEntries] = strdup(ent->d_name);
			//	nEntries++;
			}
	    }
	}
	closedir(dir);
	regfree(&re);
}

%}

%%

goal:	
	commands
	;

commands: 
	command
	| commands command 
	;

command: simple_command
        ;

simple_command:	
	/*command_and_args*/ pipe_list iomodifier_list background_optional NEWLINE {
		/* printf("   Yacc: Execute command\n"); */
		Command::_currentCommand.execute();
	}
	| NEWLINE 
	| error NEWLINE { yyerrok; }
	;

pipe_list:
	pipe_list PIPE command_and_args
	| command_and_args
	;

command_and_args:
	command_word argument_list {
		Command::_currentCommand.
			insertSimpleCommand( Command::_currentSimpleCommand );
	}
	;

argument_list:
	argument_list argument
	| /* can be empty */
	;

argument:
	WORD {
		if(strchr($1, '*') == NULL && strchr($1, '?') == NULL) {
			Command::_currentSimpleCommand->insertArgument($1);
		}
		else {
			maxEntries = 20;
			nEntries = 0;
			array = (char **) malloc(maxEntries*sizeof(char*));
			expandWildcards(NULL, $1);
			qsort(array, nEntries, sizeof(char *), cmpfunc);
			for(int i = 0; i < nEntries; i++) {
				Command::_currentSimpleCommand->insertArgument(strdup(array[i]));
			}
			free(array);
		}
		/* printf("   Yacc: insert argument \"%s\"\n", $1); */
		/*Command::_currentSimpleCommand->insertArgument( $1 );\*/
	}
	;

command_word:
	WORD {
              /* printf("   Yacc: insert command \"%s\"\n", $1); */    
	       Command::_currentSimpleCommand = new SimpleCommand();
	       Command::_currentSimpleCommand->insertArgument( $1 );
	}
	;

	/*4:19 7/6/17*/

iomodifier_list:
	iomodifier_list iomodifier_opt
	| /*empty*/
	;

	/*----end*/

iomodifier_opt:
	GREATGREAT WORD {
		/* printf("   Yacc: append output \"%s\"\n", $2); */
		if (Command::_currentCommand._outFile != 0) {
			Command::_currentCommand._ambiguity = 1;
		}
		Command::_currentCommand._append = 1;
		Command::_currentCommand._outFile = $2;
	}
	| GREAT WORD {
		/* printf("   Yacc: insert output \"%s\"\n", $2); */
		if (Command::_currentCommand._outFile != 0) {
			Command::_currentCommand._ambiguity = 1;
		}
		Command::_currentCommand._append = 0;
		Command::_currentCommand._outFile = $2;
	}
	| GREATGREATAMPERSAND WORD {
		/* printf("   Yacc: append output & \"%s\"\n", $2); */
		if (Command::_currentCommand._outFile != 0) {
			Command::_currentCommand._ambiguity = 1;
		}
		Command::_currentCommand._append = 1;
		Command::_currentCommand._outFile = $2;
		Command::_currentCommand._errFile = $2;
	}
	| GREATAMPERSAND WORD {
		/* printf("   Yacc: insert output & \"%s\"\n", $2); */
		if (Command::_currentCommand._outFile != 0) {
			Command::_currentCommand._ambiguity = 1;
		}
		Command::_currentCommand._append = 0;
		Command::_currentCommand._outFile = $2;
		Command::_currentCommand._errFile = $2;
	}
	| LESS WORD {
		if (Command::_currentCommand._inFile != 0) {
			Command::_currentCommand._ambiguity = 1;
		}
		/* printf("   Yacc: insert input \"%s\"\n", $2); */
		Command::_currentCommand._append = 0;
		Command::_currentCommand._inFile = $2;
	}
	/*|  can be empty */ 
	;

	background_optional:
	AMPERSAND {
		Command::_currentCommand._background = 1;
	}
	| /*empty*/
	;

%%

void
yyerror(const char * s)
{
	fprintf(stderr,"%s", s);
}

#if 0
main()
{
	yyparse();
}
#endif
