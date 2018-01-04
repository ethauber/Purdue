#ifndef _SYMBOL_HPP_
#define _SYMBOL_HPP_

#include <fstream>
#include <iostream>
#include <string>
#include <vector>

#include "key.hpp"

#include <map>

class Symbol {
private:
	std::vector<Key> T;
	std::vector<Key> T1;
	std::vector<Key> T2;
	std::map<Key, word_type> Thalf;

public:
	Symbol(const std::string&);
	void decrypt(const std::string&);
	std::string genString1();
	std::string genString2();
};

#endif