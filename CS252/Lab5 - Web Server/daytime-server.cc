
const char * usage =
"                                                               \n"
"daytime-server:                                                \n"
"                                                               \n"
"Simple server program that shows how to use socket calls       \n"
"in the server side.                                            \n"
"                                                               \n"
"To use it in one window type:                                  \n"
"                                                               \n"
"   daytime-server <port>                                       \n"
"                                                               \n"
"Where 1024 < port < 65536.             \n"
"                                                               \n"
"In another window type:                                       \n"
"                                                               \n"
"   telnet <host> <port>                                        \n"
"                                                               \n"
"where <host> is the name of the machine where daytime-server  \n"
"is running. <port> is the port number you used when you run   \n"
"daytime-server.                                               \n"
"                                                               \n"
"Then type your name and return. You will get a greeting and   \n"
"the time of the day.                                          \n"
"Also uses fork, thread, or pool of threads depending on flag: \n"
"daytime-server [-f|-t|-p] [<port>]                            \n";


#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <time.h>

#include <pthread.h>
#include <signal.h>
#include <sys/wait.h>

int QueueLength = 5;

// Processes time request
void processTimeRequest( int socket );

//daytime-server [<port>]
void iterativeServer( int masterSocket );

//daytime-server -f [<port>]
void forkServer( int masterSocket );

//daytime-server -t [<port>]
void createThreadForEachRequest( int masterSocket );

//daytime-server -p [<port>]
void poolOfThreads( int masterSocket );

pthread_mutex_t mutex;

extern "C" void zombieMagic(int sig) {
	while (waitpid(-1, NULL, WNOHANG) > 0);
}

int
main( int argc, char ** argv )
{
  // Print usage if not enough arguments
  //if ( argc < 2 ) {
  //  fprintf( stderr, "%s", usage );
  //  exit( -1 );
  //}

	struct sigaction sa2;
	sa2.sa_handler = zombieMagic;
	sigemptyset(&sa2.sa_mask);
	sa2.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa2, NULL)) {
		perror("sigaction Zombie");
		exit(-1);
	}


	int DEFAULTPORT = 1027;
	int flags = 0;	
	int port;
	if (argc == 1) { //use iterative and use default port -- daytime-server
		port = DEFAULTPORT;
	}
	else if (argc == 2) { //either iterative with port or flags without port
		if(atoi(argv[1]) != 0) { //atoi returns 0 when a string -> iterative with port -- daytime-server port
			port = atoi(argv[1]);
			if (port < 1024 || port > 65536) { //check for valid ports
				fprintf( stderr, "%s", usage );
				exit(-1);
			}
		}
		else { //flags without port -- daytime-server -f|-t|-p
			port = DEFAULTPORT;
			flags = 1;
		}
	}
	else if (argc == 3) { //flags with port -- daytime-server -f|-t|-p port
		port = atoi(argv[2]);
		if (port < 1024 || port > 65536) { //check for valid ports
				fprintf( stderr, "%s", usage );
				exit(-1);
		}
		flags = 1;
	}
  
  // Get the port from the arguments
  //int port = atoi( argv[1] );
  
  // Set the IP address and port for this server
  struct sockaddr_in serverIPAddress; 
  memset( &serverIPAddress, 0, sizeof(serverIPAddress) );
  serverIPAddress.sin_family = AF_INET;
  serverIPAddress.sin_addr.s_addr = INADDR_ANY;
  serverIPAddress.sin_port = htons((u_short) port);
  
  // Allocate a socket
  int masterSocket =  socket(PF_INET, SOCK_STREAM, 0);
  if ( masterSocket < 0) {
    perror("socket");
    exit( -1 );
  }

  // Set socket options to reuse port. Otherwise we will
  // have to wait about 2 minutes before reusing the sae port number
  int optval = 1; 
  int err = setsockopt(masterSocket, SOL_SOCKET, SO_REUSEADDR, 
		       (char *) &optval, sizeof( int ) );
   
  // Bind the socket to the IP address and port
  int error = bind( masterSocket,
		    (struct sockaddr *)&serverIPAddress,
		    sizeof(serverIPAddress) );
  if ( error ) {
    perror("bind");
    exit( -1 );
  }
  
  // Put socket in listening mode and set the 
  // size of the queue of unprocessed connections
  error = listen( masterSocket, QueueLength);
  if ( error ) {
    perror("listen");
    exit( -1 );
  }

//   while ( 1 ) {
	
//     Accept incoming connections
//    struct sockaddr_in clientIPAddress;
//    int alen = sizeof( clientIPAddress );
//    int slaveSocket = accept( masterSocket,
// 			      (struct sockaddr *)&clientIPAddress,
// 			      (socklen_t*)&alen);
	
//    if ( slaveSocket < 0 ) {
//      perror( "accept" );
//      exit( -1 );
//    }
	
//     Process request.
//    processTimeRequest( slaveSocket );
	
//     Close socket
//     close( slaveSocket );
//   }
  
	if(!flags) {
		iterativeServer( masterSocket );
	}
	else {
		if(!strcmp(argv[1],"-f")) {
			forkServer( masterSocket );
		}
		else if(!strcmp(argv[1], "-t")) {
			createThreadForEachRequest( masterSocket );
		}
		else if(!strcmp(argv[1], "-p")) {
			poolOfThreads( masterSocket );
		}
		else { //error in arguments
			fprintf( stderr, "%s", usage );
			exit(-1);
		}
	}

}

void
processTimeRequest( int fd )
{
  // Buffer used to store the name received from the client
  const int MaxName = 1024;
  char name[ MaxName + 1 ];
  int nameLength = 0;
  int n;

  // Send prompt
  const char * prompt = "\nType your name:";
  write( fd, prompt, strlen( prompt ) );

  // Currently character read
  unsigned char newChar;

  // Last character read
  unsigned char lastChar = 0;

  //
  // The client should send <name><cr><lf>
  // Read the name of the client character by character until a
  // <CR><LF> is found.
  //
    
  while ( nameLength < MaxName &&
	  ( n = read( fd, &newChar, sizeof(newChar) ) ) > 0 ) {

    if ( lastChar == '\015' && newChar == '\012' ) {
      // Discard previous <CR> from name
      nameLength--;
      break;
    }

    name[ nameLength ] = newChar;
    nameLength++;

    lastChar = newChar;
  }

  // Add null character at the end of the string
  name[ nameLength ] = 0;

  printf( "name=%s\n", name );

  // Get time of day
  time_t now;
  time(&now);
  char	*timeString = ctime(&now);

  // Send name and greetings
  const char * hi = "\nHi ";
  const char * timeIs = " the time is:\n";
  write( fd, hi, strlen( hi ) );
  write( fd, name, strlen( name ) );
  write( fd, timeIs, strlen( timeIs ) );
  
  // Send the time of day 
  write(fd, timeString, strlen(timeString));

  // Send last newline
  const char * newline="\n";
  write(fd, newline, strlen(newline));
  //close slave socket here so it closes in the thread and pool of thread
  close(fd);
}

//give dispatchHTTP the same function as processtimerequest for now
//#define dispatchHTTP processTimeRequest

void iterativeServer( int masterSocket ) {
	// while (1) {		
    // 		struct sockaddr_in clientIPAddress;
    // 		int alen = sizeof( clientIPAddress );
	// 	int slaveSocket = accept(masterSocket, (struct sockaddr *)&clientIPAddress, (socklen_t*)&alen);
	// 	if (slaveSocket >= 0) {
	// 		dispatchHTTP(slaveSocket);
	// 	}
	// }
	while ( 1 ) {
    //Accept incoming connections
   		struct sockaddr_in clientIPAddress;
   		int alen = sizeof( clientIPAddress );
   		int slaveSocket = accept( masterSocket,
			      (struct sockaddr *)&clientIPAddress,
			      (socklen_t*)&alen);
	
   		if ( slaveSocket < 0 ) {
     		perror( "accept" );
     		exit( -1 );
   		}
		//Process request.
		processTimeRequest( slaveSocket );
		//Close socket
		//close( slaveSocket );
  	}
}

void forkServer( int masterSocket ) {
	while (1) {
    	struct sockaddr_in clientIPAddress;
    	int alen = sizeof( clientIPAddress );
		int slaveSocket = accept(masterSocket, (struct sockaddr *)&clientIPAddress, (socklen_t*)&alen);
		if (slaveSocket >= 0) {
			int ret = fork();
			if (ret == 0) {
				processTimeRequest( slaveSocket );
				exit(0);
			}
			//close(slaveSocket);
		}
	}
}

void createThreadForEachRequest( int masterSocket ) {
	while (1) {
		pthread_t thread;
    	struct sockaddr_in clientIPAddress;
    	int alen = sizeof( clientIPAddress );
		int slaveSocket = accept(masterSocket, (struct sockaddr *)&clientIPAddress, (socklen_t*)&alen);
		if (slaveSocket >= 0) {
			//thread ends resources are recycled
			pthread_attr_t attr;
			pthread_attr_init(&attr);
			pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
			pthread_create(&thread, &attr, (void * (*)(void *))  processTimeRequest, (void *) slaveSocket);
		}
	}
}

void * loopthread (int masterSocket) {
	while(1) {
    	struct sockaddr_in clientIPAddress;
    	int alen = sizeof( clientIPAddress );
		pthread_mutex_lock(&mutex);
		int slaveSocket = accept(masterSocket, (struct sockaddr *)&clientIPAddress, (socklen_t*)&alen);
		pthread_mutex_unlock(&mutex);
		if (slaveSocket >= 0) {
			processTimeRequest( slaveSocket );
		}
	}
}

void poolOfThreads( int masterSocket ) {
	pthread_mutex_init(&mutex, NULL);
	//pthread_t t1, t2, t3, t4;
	//pthread_attr_t attr;
	//pthread_attr_init(&attr);
	//pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);
	//five threads
	pthread_t thread[5];
	//0 1 2 3 and main thread
	for (int i = 0; i < 4; i++) {
		pthread_create(&thread[i], NULL, (void * (*)(void *))  loopthread, (void *)  masterSocket);
	}
	loopthread(masterSocket);
}

