#pragma once
#include<memory>
#include<vector>
using namespace std;

class State
{
public:
	int** table;
	int size;
	int nextColor;
	int lastColor;
	vector<shared_ptr<State> > childs;
	int playerNumber;
	bool inTree = false;
	shared_ptr<State> parent;
	int depth = 0;
	int visited = 0;
	double value = 0;
	double best_value = 0;
	bool* mark;
	State();
	~State();
};

