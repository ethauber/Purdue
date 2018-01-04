
/*
 * CS252: Shell project
 *
 * Template file.
 * You will need to add more code here to execute the command table.
 *
 * NOTE: You are responsible for fixing any bugs this code may have!
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <string.h>
#include <signal.h>

#include "command.h"
//---
#include <fcntl.h>

SimpleCommand::SimpleCommand()
{
	// Create available space for 5 arguments
	_numOfAvailableArguments = 5;
	_numOfArguments = 0;
	_arguments = (char **) malloc( _numOfAvailableArguments * sizeof( char * ) );
}

void
SimpleCommand::insertArgument( char * argument )
{
	if ( _numOfAvailableArguments == _numOfArguments  + 1 ) {
		// Double the available space
		_numOfAvailableArguments *= 2;
		_arguments = (char **) realloc( _arguments,
				  _numOfAvailableArguments * sizeof( char * ) );
	}

	//implement env var expansion
	char * complete = (char *)calloc(2048, sizeof(char));
	if (strchr(argument, '$')) {
		//printf("%s\n", argument);
		int i = 0;
		int j = 0;
		while (argument[i] != '\0') {
			if (argument[i] == '$') {
				char * varName = (char *)calloc(2048, sizeof(char));
				i += 2;
				//printf("varname <= ");
				while (argument[i] != '}') {
					varName[j] = argument[i];
					//printf("%c ", argument[i]);
					j++;
					i++;
				}
				varName[j] = '\0';
				//printf("\n  varName: %s\n", varName);
				if (getenv(varName)) {
					strcat(complete, getenv(varName));
				}
				j = 0;
				free(varName);
			}
			else {
				char * others = (char *)calloc(2048, sizeof(char));
				//printf("others <= ");
				while (argument[i] != '\0' && argument[i] != '$') {
					others[j] = argument[i];
					//printf("%c ", argument[i]);
					j++;
					i++;
				}
				//printf("\n  others: %s\n", others);
				strcat(complete, others);
				j = 0;
				free(others);
				i--;
			}
			i++;
		}
		argument = strdup(complete);
		_arguments[_numOfArguments] = argument;
	}

	//implement tilde expansion
	else if (argument[0] == '~' && strlen(argument) == 1) {
		_arguments[ _numOfArguments ] = strdup(getenv("HOME"));
		//printf("%s\n", _arguments[ _numOfArguments ]);
	}
	else if(argument[0] == '~') {
		char * s = ++argument; //get rid of ~
		//printf("%s\n", s);
		const char * homes = "/homes/";
		char * complete = (char *) calloc((strlen(s) + 7), sizeof(char));
		strcat(complete, homes);
		strcat(complete, s);
		//printf("%s\n",complete);
		_arguments[ _numOfArguments ] = complete;
	}
	//default without expansion
	else {
		_arguments[ _numOfArguments ] = argument;
		//printf("%s\n", argument);
	}
	// Add NULL argument at the end
	_arguments[ _numOfArguments + 1] = NULL;
	
	_numOfArguments++;
}

Command::Command()
{
	// Create available space for one simple command
	_numOfAvailableSimpleCommands = 1;
	_simpleCommands = (SimpleCommand **)
		malloc( _numOfSimpleCommands * sizeof( SimpleCommand * ) );

	_numOfSimpleCommands = 0;
	_outFile = 0;
	_inFile = 0;
	_errFile = 0;
	_background = 0;
	_append = 0;
}

void
Command::insertSimpleCommand( SimpleCommand * simpleCommand )
{
	if ( _numOfAvailableSimpleCommands == _numOfSimpleCommands ) {
		_numOfAvailableSimpleCommands *= 2;
		_simpleCommands = (SimpleCommand **) realloc( _simpleCommands,
			 _numOfAvailableSimpleCommands * sizeof( SimpleCommand * ) );
	}
	
	_simpleCommands[ _numOfSimpleCommands ] = simpleCommand;
	_numOfSimpleCommands++;
}

void
Command:: clear()
{
	for ( int i = 0; i < _numOfSimpleCommands; i++ ) {
		for ( int j = 0; j < _simpleCommands[ i ]->_numOfArguments; j ++ ) {
			free ( _simpleCommands[ i ]->_arguments[ j ] );
		}
		
		free ( _simpleCommands[ i ]->_arguments );
		free ( _simpleCommands[ i ] );
	}

	if ( _outFile ) {
		free( _outFile );
	}

	if ( _inFile ) {
		free( _inFile );
	}

	if ( _errFile && _errFile != _outFile ){
		free( _errFile );
	}

	_numOfSimpleCommands = 0;
	_outFile = 0;
	_inFile = 0;
	_errFile = 0;
	_background = 0;
}

void
Command::print()
{
	printf("\n\n");
	printf("              COMMAND TABLE                \n");
	printf("\n");
	printf("  #   Simple Commands\n");
	printf("  --- ----------------------------------------------------------\n");
	
	for ( int i = 0; i < _numOfSimpleCommands; i++ ) {
		printf("  %-3d ", i );
		for ( int j = 0; j < _simpleCommands[i]->_numOfArguments; j++ ) {
			printf("\"%s\" \t", _simpleCommands[i]->_arguments[ j ] );
		}
	}

	printf( "\n\n" );
	printf( "  Output       Input        Error        Background\n" );
	printf( "  ------------ ------------ ------------ ------------\n" );
	printf( "  %-12s %-12s %-12s %-12s\n", _outFile?_outFile:"default",
		_inFile?_inFile:"default", _errFile?_errFile:"default",
		_background?"YES":"NO");
	printf( "\n\n" );
	
}

//access environment variables through environ
extern char ** environ;
//to return terminal to normal mode on exiting
extern "C" void tty_normal_mode(void);

void
Command::execute()
{
	// Don't do anything if there are no simple commands
	if ( _numOfSimpleCommands == 0 ) {
		prompt();
		return;
	}

	// Print contents of Command data structure
	//print();

	// Add execution here
	// For every simple command fork a new process
	// Setup i/o redirection
	// and call exec
	
	//close the shell when the user types "exit" and print to terminal goodbye
	//printf("%s\n",_simpleCommands[0]->_arguments[0]);
	if (!strcmp(_simpleCommands[0]->_arguments[0], "exit")) {
		printf("\nGood Bye!!\n\n");
		tty_normal_mode();
		exit(0);
	}
	if (!strcmp(_simpleCommands[0]->_arguments[0], "exitNoPrint")) {
		//printf("\nGood Bye!!\n\n");
		tty_normal_mode();
		exit(0);
	}
	//implement similar functionality for shell when first and only command is ~
	char * homeDir = getenv("HOME");
	if (!strcmp(_simpleCommands[0]->_arguments[0], homeDir)) {
		//char * homeDir = getenv("HOME");
		printf("myshell: %s: Is a directory\n", homeDir);
		clear();
		prompt();
		return;
	}

	//print for ambiguous action
	if (_ambiguity) {
		perror("Ambiguous output redirect.");
	}

	// ---execute
	//save in/out
	int tmpin = dup(0);
	int tmpout = dup(1);
	//save err
	int tmperr = dup(2);

	//set init input
	int fdin;
	if (_inFile) {
		fdin = open(_inFile, O_RDONLY, S_IRUSR | S_IRGRP | S_IROTH);
	}
	else {
		//use default input
		fdin = dup(tmpin);
	}

	int ret;
	int fdout;
	int fderr;
	int i;
	for (i = 0; i < _numOfSimpleCommands; i++) {
		//redirect input
		dup2(fdin, 0);
		close(fdin);

		//setup output
		if (i == _numOfSimpleCommands - 1) {
			//last simple command
			if (_outFile) {
				//need more for the case when >> instead of >
				if(_append) {//does not work here
					fdout = open(_outFile, O_WRONLY | O_APPEND | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP);
				}
				else {//works here
					//should work for >
					fdout = open(_outFile, O_WRONLY | O_TRUNC | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP);
				}
			}
			else {
				//use default output
				fdout = dup(tmpout);
			}
			if (_errFile) {
				if (_append) {
					fderr = open(_errFile, O_WRONLY | O_APPEND | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP);
				}
				else {
					fderr = open(_errFile, O_WRONLY | O_TRUNC | O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP);
				}
			}
			else {
				//use default error
				fderr = dup(tmperr);
			}
			dup2(fderr, 2);
			close(fderr);
		}
		else {
			//not last simple command
			int fdpipe[2];
			pipe(fdpipe);
			fdout = fdpipe[1];
			fdin = fdpipe[0];
		} // if/else
		//redirect output
		dup2(fdout, 1);
		//if(_errFile) {
		//  dup2(fdout, 2);
		//}
		close(fdout);

		//implement tilde expansion
		//if(!strcmp(_simpleCommands[i]->_arguments[i], "~")) {
		//	_simpleCommands[i]->_arguments[i] = getenv("HOME");
		//}
		//use .l for ~ ?

		//implement change directory
		if (!strcmp(_simpleCommands[i]->_arguments[0], "cd")){
			int errCheck = 0;
			if (_simpleCommands[i]->_numOfArguments > 1) {
				errCheck = chdir(_simpleCommands[i]->_arguments[1]);
			}
			else {
				//chdir($HOME);
				chdir(getenv("HOME"));
			}
			if (errCheck == -1) {
				//printf("myshell> cd: %s: No such file or directory \n", _simpleCommands[i]->_arguments[1]);
				//error should go to error file
				perror(_simpleCommands[i]->_arguments[1]);
			}
			continue;
		}
		//implement set environment variable
		else if (!strcmp(_simpleCommands[i]->_arguments[0], "setenv")) {
			if(_simpleCommands[i]->_numOfArguments != 3) {
				printf("Usage: setenv arg1 arg2 \n");
				continue;
			}
			setenv(_simpleCommands[i]->_arguments[1], _simpleCommands[i]->_arguments[2], 1); //the one is for overwriting
			continue;
		}
		//implement unsetting an evironment variable
		else if (!strcmp(_simpleCommands[i]->_arguments[0], "unsetenv")) {
			if(_simpleCommands[i]->_numOfArguments != 2) {
				printf("Usage: unsetenv arg1 \n");
				continue;
			}
			unsetenv(_simpleCommands[i]->_arguments[1]);
			continue;
		}
		//implement printing of environment variables
		else if (!strcmp(_simpleCommands[i]->_arguments[0], "printenv")) {
			ret = fork();
			if (ret == 0) {
				char **p = environ;
				while (*p != NULL) {
					printf("%s\n", *p);
					p++;
				}
				exit(0);
			}
			else if (ret < 0) {
				perror("fork");
				exit(1);
			}
			continue;
		}

		//create child process
		ret = fork();
		if (ret == 0) {
			execvp(_simpleCommands[i]->_arguments[0], _simpleCommands[i]->_arguments);
			perror("execvp");
			_exit(1); //want _exit() instead of exit() because we don't want to flush buffers
		}
		else if (ret < 0) {
			perror("fork");
			exit(1);
		}
	} //for

	//restore in/out defaults
	dup2(tmpin, 0);
	dup2(tmpout, 1);
	close(tmpin);
	close(tmpout);

	//restore err
	dup2(tmperr, 2);
	close(tmperr);

	if (!_background) {
		//wait for last command
		waitpid(ret, NULL, 0);
	}
	// ---execute

	// Clear to prepare for next command
	clear();
	
	// Print new prompt
	prompt();
}

// Shell implementation

void
Command::prompt()
{
	if(isatty(0)) {
		if (getenv("PROMPT")) {
			printf("%s>", getenv("PROMPT"));
		}
		else {
			printf("myshell>");
		}
		fflush(stdout);
	}
}

//added 7/11/2017 11:58 a.m. for ctrl-c    modified 7/16/17 555
extern "C" void disp(int sig)
{
	putchar('\n');
	Command::_currentCommand.clear();
	Command::_currentCommand.prompt();
}

extern "C" void zombieMagic(int sig) {
	while(waitpid(-1, NULL, WNOHANG) > 0);
}

Command Command::_currentCommand;
SimpleCommand * Command::_currentSimpleCommand;

int yyparse(void);

main()
{
	/* 7/11/2017 11:20 a.m. - ctrl-c  modified 7/16/17*/
	//call prompt again if ctrl-c signal is found
	struct sigaction sa;
	sa.sa_handler = disp;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	if (sigaction(SIGINT, &sa, NULL)) {
		perror("sigaction");
		exit(2);
	}

	struct sigaction sa2;
	sa2.sa_handler = zombieMagic;
	sigemptyset(&sa2.sa_mask);
	sa2.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa2, NULL)) {
		perror("sigaction Zombie");
		exit(-1);
	}


	Command::_currentCommand.prompt();
	yyparse();
}

