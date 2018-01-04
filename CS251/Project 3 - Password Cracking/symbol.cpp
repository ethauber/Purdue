#include <cstdlib>
#include <fstream>
#include <iostream>
#include <string>

#include "key.hpp"
#include "symbol.hpp"

//#include "timer.hpp"

std::string me;
std::string encrypted;
std::string table_filename;
bool verbose = false;

//std::string guess;
word_type guess;
word_type guess2;
Key encryptedK;
//CPU_timer t;
//unsigned long long total = 0;
//unsigned long long total2 = 0;
int upper = C/2;
int lower = C - upper;
//unsigned long long limit = pow(32,(C/2));
//int split() {//split table for lower half
		
//}

Symbol::Symbol(const std::string& filename) {
 	
	int T1UL = N/2; //T1 upper limit
 
 	while(T1UL % B != 0) { //keeps bits in range so chars still map
 		T1UL--;
    }
 
 	T.resize(N);
 	T1.resize(N);
 	T2.resize(N);
	std::string buffer;
    std::fstream input(filename.c_str(), std::ios::in);
    for (int i = 0; i < N; i++) {
        std::getline(input, buffer);
        T[i].set_string(buffer);
     	if(i < T1UL) {
      		T1[i] = T[i];
     	}
     	else {
     		T2[i] = T[i];
     	}
    }
    input.close();
	// insert your code here
    //first half
 	//std::cout << "upper=" << upper << " lower=" << lower << std::endl;
 	unsigned long long tempTotal;
 	//std::cout << "pow(32,upper)=" << pow(32,upper) << std::endl;
    for(unsigned long long i = 0; i < pow(32,upper); i++) {
     	tempTotal = i;
     	for(int j = 0; j < C/2; j++) {
     		guess[j] = tempTotal%32;
         	//std::cout << ALPHABET[tempTotal%32];
         	tempTotal = tempTotal >> B;
        }
     	//total++;
     	//Key(guess).show();
     	Key ss = Key(guess).subset_sum(T, false);
     	Thalf.emplace(ss, guess);
    }
}

void Symbol::decrypt(const std::string& encrypted){
	// insert your code here
 	//unsigned long long upper;

 	//std::cout << "upper=" << upper << " || lower=" << lower;
 	
 	unsigned long long tempTotal;
    for(unsigned long long i = 0; i < pow(32,lower); i++) {
    	//find guesses
     	tempTotal = i/*total*/;
     	for(int j = 0; j < lower; j++) {
     		guess2[j + lower - C%2] = tempTotal%32;
         	//std::cout << ALPHABET[tempTotal%32];
         	tempTotal = tempTotal >> B;
        }
     	//total++;
     	//do subtraction
     	Key temp = encryptedK;
     	Key guessK = Key(guess2);
     	//guessK.show();
     	Key ss = guessK.subset_sum(T2, false);
     	temp -= ss;
     	
     	//print if found
     	std::map<Key, word_type>::iterator it = Thalf.find(temp);
     	if(it != Thalf.end()) {
    		//Key(it->first).showSNE();
         	guessK += Key(it->second);
         	guessK.showS();
    	}
    }
}

void usage(const std::string& error_msg="") {
	if (!error_msg.empty()) std::cout << "ERROR: " << error_msg << '\n';
	std::cout << me << ": Symbol table-based cracking of Subset-sum password"
		<< " with " << B << " bits precision\n"
	    << "USAGE: " << me << " <encrypted> <table file> [options]\n"
		<< "\nArguments:\n"
		<< " <encrypted>:   encrypted password to crack\n"
		<< " <table file>:  name of file containing the table to use\n"
		<< "\nOptions:\n"
		<< " -h|--help:     print this message\n"
		<< " -v|--verbose:  select verbose mode\n\n";
	exit(0);
}

void initialize(int argc, char* argv[]) {
	me = argv[0];
	if (argc < 3) usage("Missing arguments");
	encrypted = argv[1];
	table_filename = argv[2];
	for (int i=3; i<argc; ++i) {
		std::string arg = argv[i];
		if (arg == "-h" || arg == "--help") usage();
		else if (arg == "-v" || arg == "--verbose") verbose = true;
		else usage("Unrecognized argument: " + arg);
	}
}

int main(int argc, char *argv[]){
	initialize(argc, argv);
	// insert your code here
 	encryptedK = Key(encrypted);
 	//t.tic();
 	Symbol s(table_filename);
 	s.decrypt(encrypted);
	//t.toc();
 	//std::cout << "time for C:" << C << " = " << t.elapsed() << std::endl;
	return 0;
}