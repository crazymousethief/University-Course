/**
 * @Author: crazymousethief
 * @Date:   5/29/17
 */

#ifndef DATASTRUCTURELAB_MENU_H
#define DATASTRUCTURELAB_MENU_H

#include <string>
#include <vector>
#include <functional>

class Menu {
private:
    std::string content;
public:
    Menu(const std::string &content);

    virtual ~Menu();

    virtual const std::string &getContent() const;

    void setContent(const std::string &content);
};

class LeafMenu : public Menu {
private:
    std::function<void()> fn;
public:
    LeafMenu(const std::string &content, const std::function<void()> &fn);

    LeafMenu(const std::string &content);

    virtual ~LeafMenu();

    void call();
};

class ParentMenu : public Menu {
private:
    std::vector<Menu *> v;
    unsigned long maxMenuSize = 4;

    void displayMenu();

public:
    ParentMenu(const std::string &content, const std::initializer_list<Menu *> &list);

    ParentMenu(const std::initializer_list<Menu *> &list);

    virtual ~ParentMenu();

    void run();
};

#endif //DATASTRUCTURELAB_MENU_H
