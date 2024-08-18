package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

import static com.fullteaching.e2e.no_elastest.common.Constants.LOCALHOST;
import static java.lang.System.getProperty;

public class SpiderNavigation {

    /*NOTE: traditional spider navigation retrieving all links and then confirm them doesn't
     * 		work properly in this kind of application.
     * This is a Specialized Spider navigation for FullTeaching.*/


    private static String host = LOCALHOST;

    /**
     * Retrieve all the links in a webpage
     *
     * @param wd WebDriver with the operation
     * @return List<WebElements>
     */
    public static List<WebElement> getPageLinks(WebDriver wd) { //29 lines
        String appHost = getProperty("fullTeachingUrl");
        if (appHost != null) {
            host = appHost;
        }
        Set<String> links_href = new HashSet<>();
        List<WebElement> links = new ArrayList<>();
        List<WebElement> a_lst = wd.findElements(By.tagName("a"));
        for (WebElement a : a_lst) {
            String href = a.getAttribute("href");
            if ((href != null) && (!href.trim().isEmpty()) && (!href.contains("#")) && isContainedIn(href.trim(), links_href) && href.contains(host))
                    links.add(a);
        }
        return links;
    }

    /**
     * Returns all the unexplored links of a web page
     *
     * @param wd       WebDriver with the operation
     * @param explored Dictionary with the proposed links to explore
     */
    public static List<WebElement> getUnexploredPageLinks(WebDriver wd, Map<String, String> explored) { //36 lines
        List<WebElement> links = new ArrayList<>();
        List<WebElement> allLinks = getPageLinks(wd);
        for (WebElement a : allLinks) {
            String href = a.getAttribute("href");
            if ((href != null) && (!href.trim().isEmpty()) && (!href.contains("#")) && isContainedIn(href.trim(), explored.keySet()) && href.contains(host))
                links.add(a);
        }
        return links;
    }

    public static Map<String, String> exploreLinks(WebDriver wd, List<WebElement> pageLinks, Map<String, String> explored, int depth) { //49 lines
        if (depth <= 0) return explored;
        while (!pageLinks.isEmpty()) {
            WebElement link = pageLinks.get(0);
            String href = link.getAttribute("href");
            String currentUrl = wd.getCurrentUrl();
            boolean explore = true;
            try {
                //explore page...
                //navigate to new page:
                link.click();
                Wait.footer(wd);
                explored.put(href, "OK");
            } catch (Exception e) {
                //if fails put KO and continue
                explored.put(href, "KO");
                explore = false;
            }
            if (explore) {
                List<WebElement> newLinks = getUnexploredPageLinks(wd, explored);// 9 lines
                explored = exploreLinks(wd, newLinks, explored, depth - 1);
            }
            NavigationUtilities.getUrlAndWaitFooter(wd, currentUrl); //3 lines
            pageLinks = SpiderNavigation.getUnexploredPageLinks(wd, explored);//9lines
        }
        return explored;
    }

    private static boolean isContainedIn(String href, Set<String> set) { // 8lines
        if (set.contains(href)) return false;
        String aux_href;
        if (href.endsWith("/")) {
            aux_href = href.substring(0, href.length() - 1);
        } else {
            aux_href = href + "/";
        }
        return !set.contains(aux_href);
    }

    public static Set<String> addNonExistentLink(Set<String> original, String href) { //5lines
        if ((href != null) && (!href.isEmpty()) && (!href.contains("#")) && isContainedIn(href, original) && href.contains(host))
                original.add(href);
        return original;
    }

    public static List<String> discardExplored(List<String> new_links, Set<String> explored) { //8 lines
        List<String> withOutExplored = new ArrayList<>();
        for (String href : new_links) {
            if ((href != null) && (!href.isEmpty()) && (!href.contains("#")) && isContainedIn(href, explored) && href.contains(host))
                    withOutExplored.add(href);
        }
        return withOutExplored;
    }

}
