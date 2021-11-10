#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <vector>
#include <chrono>
#include <math.h>
#include <string>
#include <fstream>
#include <iomanip>
#include <sstream>
#define size 10
#define player 5

using namespace std;
using namespace std::chrono;

short model_number = 1;


struct value {
	int num;
	double value;
	double best_value;
	bool* mark;

};

struct state {
	bool in_tree;
	struct state* parent;
	struct value* value;
	int depth;
	int** table;
	int size;
	int next_color;
	int last_color;
	int player_number;
	vector<pair<int, int> > last_move;
	vector<pair<int, int> > target;
	vector<struct state*> childs;

};

//vector<struct state*> allPointers;

struct game {
	struct state* myState;
}g;
int ass = 0;
int boz = 0;
int wtf = 0;
float near_time = 0;
float KISS = 0;
float PISS = 0;
float new_time = 0;
int jesus = 0;
float game_end=0;
int shunt = 0;

void set_state(struct state* s) {
	s->in_tree = false;
	s->depth = 0;
	s->childs_deleted = false;
	s->value = NULL;
	s->size = 5;
	s->player_number = 5;
	s->deleted = false;
	s->table = new int*[s->size];
	for (int i = 0; i < s->size; i++) {
		s->table[i] = new int[s->size];
		for (int j = 0; j < s->size; j++) {
			s->table[i][j] = 0;
		}
	}
	for (int i = 0; i <= s->player_number; ++i) {
		s->last_move.push_back(pair<int, int>(-1, -1));
		s->target.push_back(pair<int, int>(-1, -1));
	}

	s->last_move[1].first = 0;
	s->last_move[1].second = 0;

	s->target[1].first = 2;
	s->target[1].second = 1;

	s->last_move[2].first = 1;
	s->last_move[2].second = 0;

	s->target[2].first = 3;
	s->target[2].second = 2;

	s->last_move[3].first = 2;
	s->last_move[3].second = 2;

	s->target[3].first = 0;
	s->target[3].second = 4;

	s->last_move[4].first = 1;
	s->last_move[4].second = 3;

	s->target[4].first = 4;
	s->target[4].second = 4;

	s->last_move[5].first = 4;
	s->last_move[5].second = 0;

	s->target[5].first = 2;
	s->target[5].second = 3;

	s->table[0][0] = 1;
	s->table[2][1] = 1;
	s->table[1][0] = 2;
	s->table[3][2] = 2;
	s->table[2][2] = 3;
	s->table[0][4] = 3;
	s->table[1][3] = 4;
	s->table[4][4] = 4;
	s->table[4][0] = 5;
	s->table[2][3] = 5;
	s->last_color = -1;
	s->next_color = 1;
	s->parent = NULL;
}

bool has_child(struct state* s) {
	//	printf("has child method\n");
//	if (s == NULL) {
//		return false;
//	}
	//	printf("my next color is: %d\n", s->next_color);
//	auto start = high_resolution_clock::now();
	for (int i = -1; i < 2; ++i) {
		for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
			if (s->last_move[s->next_color].first + i >= 0 && s->last_move[s->next_color].first + i < s->size && s->last_move[s->next_color].second + j >= 0 && s->last_move[s->next_color].second + j < s->size && s->table[s->last_move[s->next_color].first + i][s->last_move[s->next_color].second + j] == 0) {
//				auto stop = high_resolution_clock::now();
//				auto duration = duration_cast<microseconds>(stop - start);
//				printf("has child in true: %.4f\n", duration/(double)1000000);	
				return true;
			}
		}
	}
	//					printf("WTFWTFWTF\n");
	
	return false;
}

bool near(struct state* s, int color) {
	boz++;
	auto start = high_resolution_clock::now();
	bool x = abs(s->last_move[color].first - s->target[color].first) + abs(s->last_move[color].second - s->target[color].second) == 1;
	auto stop = high_resolution_clock::now();
	auto duration = duration_cast<microseconds>(stop - start);
	near_time += duration.count() / (double)1000000;
	return x;
}

bool game_not_ended(struct state* s) {
	int res = 0;
	ass++;
	auto start = high_resolution_clock::now();
	
	//	printf("game not ended method\n");
	for (int i = 1; i <= s->player_number; ++i) {
		res += near(s, i) ? 1 : 0;
	}
	//	printf("s is: %p\n", s);
	//	printf("s players are: %d\n", s->player_number);
	//	printf("result is: %d\n", res);
	if (res == s->player_number) {
		auto stop = high_resolution_clock::now();
		auto duration = duration_cast<microseconds>(stop - start);
		game_end += duration.count() / (double)1000000;
//		printf("game not ended time false: %.6f\n", selection_time);
		return false;
	}
//	getchar();
	if (!has_child(s)) {
		//		printf("oh shit");
		auto stop = high_resolution_clock::now();
		auto duration = duration_cast<microseconds>(stop - start);
		game_end += duration.count() / (double)1000000;
//		printf("game not ended false has child: %.6f\n", selection_time);
		return false;
	}
	auto stop = high_resolution_clock::now();
	auto duration = duration_cast<microseconds>(stop - start);
	game_end += duration.count() / (double)1000000;
//	printf("game not ended time true: %.6f\n", selection_time);
	return true;
}

void print_state(struct state* st) {
	printf("print state method\n");
	string s = "{";
	for (int i = 0; i < st->size; i++) {
		for (int j = 0; j < st->size; j++) {
			stringstream ss;
			ss << setw(2) << setfill(' ') << st->table[i][j];
			string sis = ss.str();
			s += sis + ", ";
		}

		s += (i != st->size - 1 ? "\n " : "");
	}
	s += "}\n***************************\n";
	printf("%s", s.c_str());
}

void del(struct state* s) {
	if (!s) return;
	for (int i = 0; i < s->size; i++) {
		delete (s->table[i]);
	}
	delete (s->table);
	s->table = NULL;
	delete (s->value->mark);
	delete (s->value);
	s->value = NULL;
	for (int i = 0; i < s->childs.size(); i++) {
		del(s->childs[i]);
	}
	s->childs.clear();
	delete (s);
	s = NULL;
}

struct state* reset(struct state* st) {
	//	printf("reset method\n");
	struct state* titi = st;
	while (titi) {
		titi = titi->parent;
	}
	del(titi);
	delete (titi);
	st->in_tree = false;
	st->parent = NULL;
	st->value = NULL; 
	st->childs.clear();
	st->last_color = -1;
	return st;
}

int child_number(struct state* s) {
	int ans = 0;
	for (int i = -1; i < 2; ++i) {
		for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
			if (s->last_move[s->next_color].first + i >= 0 && s->last_move[s->next_color].first + i < s->size && s->last_move[s->next_color].second + j >= 0 && s->last_move[s->next_color].second + j < s->size && s->table[s->last_move[s->next_color].first + i][s->last_move[s->next_color].second + j] == 0) {
				++ans;
			}
		}
	}
	return ans;
}

int child_number_target(struct state* s) {
	pair<int, int> temp = s->last_move[s->next_color];
	s->last_move[s->next_color] = s->target[s->next_color];
	s->target[s->next_color] = temp;

	int res = child_number(s);
	temp = s->last_move[s->next_color];
	s->last_move[s->next_color] = s->target[s->next_color];
	s->target[s->next_color] = temp;
	return res;
}

void set_next_color(struct state* s) {
	double best = 1000000;
	int bestColor = s->last_color % s->player_number + 1;
	for (int i = 1; i <= s->player_number; ++i) {
		s->next_color = (s->last_color + i - 1) % s->player_number + 1;
		int cn = child_number(s);
		int cnt = child_number_target(s);
		if (near(s, s->next_color) || cn == 0 || cnt == 0)
			continue;
		if (cn == 1) {
			bestColor = s->next_color;
			break;
		}
		if (cnt == 1) {
			bestColor = s->next_color;
			pair<int, int> temp = s->last_move[s->next_color];
			s->last_move[s->next_color] = s->target[s->next_color];
			s->target[s->next_color] = temp;
			break;
		}
		if (best > cn) {
			best = cn;
			bestColor = s->next_color;
		}
		else if (best == cn) {

		}
	}
	s->next_color = bestColor;
}

void set_set_file_state(struct state* s, string st) {
	s->value = NULL;
	shunt++;
	s->childs_deleted = false;
	s->in_tree = false;
	s->deleted = false;
	ifstream input(st);
	int size;
	int player_number;
	input >> size >> player_number;
	s->player_number = player_number;
	s->size = size;
	s->table = new int*[size];
	for (int i = 0; i < size; i++) {
		s->table[i] = new int[size];
		for (int j = 0; j < size; ++j) {
			s->table[i][j] = 0;
		}
	}
	for (int i = 0; i <= s->player_number; ++i) {
		s->last_move.push_back(pair<int, int>(-1, -1));
		s->target.push_back(pair<int, int>(-1, -1));
	}


	for (int i = 0; i < size; ++i) {
		for (int j = 0; j < size; ++j) {
			input >> s->table[i][j];
			if (s->table[i][j] != 0)
				if (s->last_move[s->table[i][j]].first == -1 && s->last_move[s->table[i][j]].second == -1) {
					s->last_move[s->table[i][j]].first = i;
					s->last_move[s->table[i][j]].second = j;
				}
				else {
					s->target[s->table[i][j]].first = i;
					s->target[s->table[i][j]].second = j;
				}
		}
	}
	s->last_color = player_number;
	set_next_color(s);
	s->parent = NULL;
	s->last_color = -1;

}

struct game start_game(struct game gg, int argc, char* argv[]) {
	//	printf("oh shit!\n");
	gg.myState = new struct state;
//	allPointers.push_back(gg.myState);
	if (argc == 1) {
		set_state(gg.myState);
	}
	else {
		set_set_file_state(gg.myState, argv[argc - 1]);
	}
	return gg;
}

struct state* new_state(struct state* s, struct state* st, int x, int y, int c) {
	//	printf("creating new state\n");
	s->size = st->size;
	int size = s->size;
	s->player_number = st->player_number;
	
	auto start = high_resolution_clock::now();
	
	s->table = new int*[size];
	
	for (int i = 0; i < size; i++)
	{
		s->table[i] = new int[size];
	}
	
	auto stop = high_resolution_clock::now();
	auto duration = duration_cast<microseconds>(stop - start);
	KISS += duration.count() / (double)1000000;
	
	start = high_resolution_clock::now();
	for (int i = 0; i < size; i++)
	for (int j = 0; j < size; ++j)
			s->table[i][j] = st->table[i][j];
	s->table[y][x] = c;
	stop = high_resolution_clock::now();
	duration = duration_cast<microseconds>(stop - start);
	PISS += duration.count() / (double)1000000;

	s->last_move.push_back(pair<int, int>(-1, -1));
	s->target.push_back(pair<int, int>(-1, -1));
	for (int i = 1; i <= s->player_number; ++i) {
		s->last_move.push_back(st->last_move[i]);
		s->target.push_back(st->target[i]);
	}

		
	s->last_move[c] = pair<int, int>(y, x);
	s->parent = st;
	s->last_color = st->next_color;
	set_next_color(s);
	s->depth = st->depth + 1;
	s->in_tree = false;

}

struct state* simulateX(struct state* st,  int x, int y, int c) {
	//	printf("in simulatex\n");
	struct state* res = NULL;

//	allPointers.push_back(st);
	jesus++;
	if (st->table[y][x] == 0) {
		//		printf("in if\n");
			wtf++;

		auto start = high_resolution_clock::now();
		res = new struct state;
//		allPointers.push_back(res);
		res = new_state(res, st, x, y, c);
		auto stop = high_resolution_clock::now();
		auto duration = duration_cast<microseconds>(stop - start);
		new_time += duration.count() / (double)1000000;

		//		printf("result is:\n");
		//		print_state(res);
	}
	return res;
}

vector<struct state*> refresh_childs(struct state* st) {
//		printf("in refresh childs\n");
	vector<struct state*> childss;
	for (int i = -1; i < 2; ++i) {
		for (int j = (i == 0 ? -1 : 0); j < (i == 0 ? 2 : 1); ++j) {
			if (st->last_move[st->next_color].first + i >= 0 && st->last_move[st->next_color].first + i < st->size && st->last_move[st->next_color].second + j >= 0 && st->last_move[st->next_color].second + j < st->size && st->table[st->last_move[st->next_color].first + i][st->last_move[st->next_color].second + j] == 0) {
//				printf("%d, %d\n", st->last_move[st->next_color].first, st->last_move[st->next_color].second);
				childss.push_back(simulateX(st, st->last_move[st->next_color].second + j, st->last_move[st->next_color].first + i, st->next_color));
//				allPointers.push_back(childss[childss.size() - 1]);
			}
		}
	}
//		printf("out of refreshing child\n");
	return childss;
}

vector<struct state*> get_childs(struct state* st) {

//	allPointers.push_back(st);
	//	printf("I definitely ain\'t here\nhere is get childs\n");
	//	print_state(st);
	//	printf("am i in tree: %s\n", ((st->in_tree)?"true":"false"));
	if (st->in_tree) {
		//		printf("size of my childs: %d\n", st->childs.size());
		if (st->childs.empty()) {
			st->childs = refresh_childs(st);
		}
		return st->childs;
	}
	//	printf("well well\n");
	return refresh_childs(st);
}

int compareTo_UCT(struct value* vbest, struct value v, int total_number) {
	double u1 = vbest->value + sqrt(2 * log(total_number) / vbest->num);
	double u2 = v.value + sqrt(2 * log(total_number) / v.num);
	if (u1 < u2) {
		return -1;
	}
	else {
		return 1;
	}
}

struct state* best_uct(struct state* st) {
	struct value* vx = st->value;

//	allPointers.push_back(st);
	//	printf("Am I here?\nfinding best uct");
//	auto start = high_resolution_clock::now();
	vector<struct state*> childs = get_childs(st);
//	auto stop = high_resolution_clock::now();
//	auto duration = duration_cast<microseconds>(stop - start);		
//	printf("has getchilds in selection: %.4f\n", duration/(double)1000000);
//	for (int i = 0; i < childs.size(); i++) {
//		allPointers.push_back(childs[i]);
//	}
	//	printf("out of get childs in bestuct\n");
	struct state* ans = NULL;
	struct value* vbest = NULL;

	for (int i = 0; i < childs.size(); i++) {
		struct state* st = childs[i];
//		allPointers.push_back(st);
		if (!st->in_tree)
			return st;
		struct value* vv = st->value;
		if (vbest == NULL || compareTo_UCT(vbest, *vv, vx->num) < 0) {
			vbest = vv;
			ans = st;
		}
	}
	return ans;
}

struct state* selection(struct state* st) {
//	printf("into selection\n");
//	float selection_time = 0;
	
	while (st->in_tree && game_not_ended(st)) {
//		getchar();
//		allPointers.push_back(st);
//		auto start = high_resolution_clock::now();

//	auto start = high_resolution_clock::now();
		st = best_uct(st);
		
//	auto stop = high_resolution_clock::now();
//	auto duration = duration_cast<microseconds>(stop - start);
//	selection_time += duration.count() / (double)1000000;	
	}
	
//	printf("uct time true: %.6f\n", selection_time);
//	printf("out of selection\n");
	return st;
}

struct state* expansion(struct state* s) {
	//	printf("into expansion\n");
//	allPointers.push_back(s);

	if (!s->in_tree) {
		s->in_tree = true;
		s->value = new struct value;
		s->value->num = 0;
		s->value->value = 0;
		s->value->best_value = 0;
	}
	else if (game_not_ended(s)) {
		printf("*******We should not be here!******");
	}
	//	printf("out of expansion\n");
	return s;
}

struct state* get_random_child(struct state* s) {
	//	printf("get randomchilds\n");
//	allPointers.push_back(s);

	vector<struct state*> childss = get_childs(s);
//	for (int i = 0; i < childss.size(); i++) {
//		allPointers.push_back(childss[i]);
//	}
	//	printf("out of get childs in random\n");
	srand(time(0));
	//	printf("size of all childs is: %d\n", childss.size());
	if (childss.empty()) {
		return NULL;
	}
	int v = rand() % childss.size();
	//	printf("choosing this child: %d\n", v);
	//	print_state(childss[v]);
	return childss[v];
}

struct value* get_value(struct state* s) {
	//	printf("getting the fucking value\n");
//	allPointers.push_back(s);

	if (game_not_ended(s)) {
		//		printf("we should not at all be here!\n");
		return NULL;
	}
	int res = 0;
	bool m[s->player_number + 1];
	m[0] = false;
	for (int i = 1; i <= s->player_number; i++) {
		m[i] = near(s, i);
		if (m[i]) {
			res += 1;
		}
		//		printf("%s ", (m[i]?"true":"false"));
	}
	struct value* v = new struct value;
	v->num = -1;
	v->value = ((float)res / s->player_number);
	//	printf("%d/%d=%f\n",res,s->player_number, v->value);
	v->best_value = v->value;
	v->mark = m;
	return v;
}

struct value* rollout(struct state* s) {
	//	printf("I\'m in rollout\n");
	float roll = 0;
	float game = 0;
	while (true) {
//		printf("game not ended: %s\n",((game_not_ended(s))?"true":"false"));
		
//		auto start = high_resolution_clock::now();
		if(!game_not_ended(s)){
			break;
		}
//		auto stop = high_resolution_clock::now();
//		auto duration = duration_cast<microseconds>(stop - start);
//		game += duration.count() / (double)1000000;
//		getchar();
//		allPointers.push_back(s);
		//		printf("finding a state which means the game has ended!\n");
//		auto starty = high_resolution_clock::now();
		s = get_random_child(s);
		//		print_state(s);
//		auto stopy = high_resolution_clock::now();
//		auto durationy = duration_cast<microseconds>(stopy - starty);
//		roll += durationy.count() / (double)1000000;
	}
//	printf("roll = %.4f\n", roll);
//	printf("game = %.4f\n", game);
//	printf("============================================\n");
	//	printf("we found it!\n");
	return get_value(s);
}

struct value* update(struct value* v, struct state* st, struct value* simulation_result) {
	v->num++;
//	allPointers.push_back(st);

	v->best_value = fmax(v->value, simulation_result->value - (st->last_color != -1 ? simulation_result->mark[st->last_color] ? (double)(3 - model_number / 2)*(1 / st->player_number) : 0 : 0));
	//	printf("in update\n");
	//	printf("%f", v->best_value);
	switch (model_number)
	{
	case 1:
	case 2:
	case 3:
		v->value = (v->value * (v->num - 1) + simulation_result->value - (st->last_color != -1 ? simulation_result->mark[st->last_color] ? (double)(1.5 - model_number / 2) * (1 / st->player_number) : 0 : 0)) / v->num;
		break;
	case 4:
	case 5:
	case 6:
		v->value = v->best_value;
	default:
		break;
	}
	return v;
}

void backpropagation(struct value* simulation_result, struct state* state) {
	while (state != NULL) {
//		allPointers.push_back(state);
		if (state->in_tree) {
			state->value = update(state->value, state, simulation_result);
		}
		state = state->parent;
	}
}

int compareTo(struct value* vbest, value v) {
	if (vbest->value < v.value) {
		return -1;
	}
	else {
		return 1;
	}
}

struct state* bestChild(struct state* s) {
	//	printf("finding best child\n");

//	allPointers.push_back(s);

	vector<struct state*> childs = get_childs(s);
//	for (int i = 0; i < childs.size(); i++) {
//		allPointers.push_back(childs[i]);
//	}
	//	printf("out of get all childs\n");
	struct state* ans = NULL;
	struct value* vbest = NULL;
	for (int i = 0; i < childs.size(); i++) {
		struct state* ch = childs[i];
//		allPointers.push_back(ch);
		struct value* vv = ch->value;
		if (vbest == NULL || compareTo(vbest, *vv) < 0) {
			vbest = vv;
			ans = ch;
		}
	}
	return ans;
}

struct state* getBestNextState(struct state* st) {
//	allPointers.push_back(st);
	//	printf("best next state method\n");
	st = reset(st);
//	allPointers.push_back(st);
	float selection_time=0, epansion_time=0, rollout_time=0, backprop_time=0;
	for (int i = 0; i < 2000; i++) {
		//		printf("i = %d", i);
		//				getchar();

		//		printf("***********************************\n***********************************\nstate is:\n");
		//		print_state(st);
		//		printf("============================\n");
		auto start = high_resolution_clock::now();
		struct state* leaf = selection(st);
		auto stop = high_resolution_clock::now();
		auto duration = duration_cast<microseconds>(stop - start);
		selection_time += duration.count() / (double)1000000;
//		allPointers.push_back(leaf);
		//		printf("leaf is:\n");
		//		print_state(leaf);
		//		printf("after selection\n");
		start = high_resolution_clock::now();
		struct state* expandedLeaf = expansion(leaf);
		stop = high_resolution_clock::now();
		duration = duration_cast<microseconds>(stop - start);
		epansion_time += duration.count() / (double)1000000;
//		allPointers.push_back(expandedLeaf);
		//		printf("expanded leaf is:\n");
		//		print_state(expandedLeaf);
		//		printf("============================\n");
		//		printf("after expansion\n");
		start = high_resolution_clock::now();
		struct value* simulationResult = rollout(expandedLeaf);
		stop = high_resolution_clock::now();
		duration = duration_cast<microseconds>(stop - start);
		rollout_time += duration.count() / (double)1000000;
		//		printf("simulation result is: %f\n", simulationResult->value);
		//		printf("after rollout\n");
		start = high_resolution_clock::now();
		backpropagation(simulationResult, expandedLeaf);
		stop = high_resolution_clock::now();
		duration = duration_cast<microseconds>(stop - start);
		backprop_time += duration.count() / (double)1000000;
		//		printf("after backprop\n");
	}
	printf("selection time: %.4f, expansion time is: %.4f, simulation time is: %.4f, backprop time is: %.4f\n", selection_time, epansion_time, rollout_time, backprop_time);
	return bestChild(st);
}

void run(struct game gg, int argc, char* argv[]) {

	gg = start_game(gg, argc, argv);
	clock_t t;
	t = clock();
	auto start = high_resolution_clock::now();
	while (game_not_ended(gg.myState)) {
		//		print_state(gg.myState);
		struct state* st = getBestNextState(gg.myState);
//		allPointers.push_back(st);
		gg.myState = st;
	}
	t = clock() - t;
	auto stop = high_resolution_clock::now();
	auto duration = duration_cast<microseconds>(stop - start);
	//	printf("while ended\n");
	print_state(gg.myState);

	printf("Model Number: %d, Time: %.2f||%.2f seconds, Ratio: {Num: %d, value: %f, max: %f}\n", model_number, ((double)t) / CLOCKS_PER_SEC, duration.count() / (double)1000000, gg.myState->value->num, gg.myState->value->value, gg.myState->value->best_value);
//	struct state* titi = gg.myState;
//	while (titi) {
//		titi = titi->parent;
//	}
//	del(titi);
//	delete (titi);
//	printf("this shit: %d\n", allPointers.size());
//	getchar();
//	for (int i = 0; i < allPointers.size(); i++) {
//		if (!allPointers[i]) {
//			struct state* titis = allPointers[i];
//			while (titis) {
//				titis = titis->parent;
//			}
//			del(titis);
//			delete (titis);
//		}
//	}
//	allPointers.clear();
//	getchar();
}

int main(int argc, char* argv[]) {
	
	
	for (int i = 1; i <= 6; i++) {
		printf("%d\n", i);
		model_number = i;
		if(atoi(argv[1])==0){
			run(g, argc, argv);
		}
		else if(atoi(argv[1])==i){
			run(g, argc, argv);
			break;
		}
		else{
			continue;
		}
		printf("======================================\n");
	}
	printf("game ended: %.5f\n", game_end);
	printf("near time: %.5f\n", near_time);
	printf("new time: %.5f\n", new_time);
	printf("KISS time: %.5f\n", KISS);
	printf("PISS time: %.5f\n", PISS);
	
	printf("how many calls of game ended: %d\n", ass);
		printf("how many calls of jesus: %d\n", jesus);
		printf("how many calls of new state: %d\n", wtf);
		printf("how many calls of shunt: %d\n", shunt);
	printf("how many calls of near time: %d\n", boz);
	return 0;
}
