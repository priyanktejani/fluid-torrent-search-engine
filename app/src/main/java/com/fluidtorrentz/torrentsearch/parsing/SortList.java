/*
 * Copyright (c) 2020 Priyank Tejani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fluidtorrentz.torrentsearch.parsing;

public class SortList {
    private String sortOneThree;
    private String sortLimeTorrents;

    public SortList(String sortItem) {
        switch (sortItem) {
            case "Seeds DESC":
                sortOneThree = "/seeders/desc/";
                sortLimeTorrents = "/seeds/";
                break;
            case "Seeds ASC":
                sortOneThree = "/seeders/asc/";
                sortLimeTorrents = "";
                break;
            case "Leeches DESC":
                sortOneThree = "/leechers/desc/";
                sortLimeTorrents = "/leechs/";
                break;
            case "Leeches ASC":
                sortOneThree = "/leechers/asc/";
                sortLimeTorrents = "";
                break;
            case "Size DESC":
                sortOneThree = "/size/desc/";
                sortLimeTorrents = "/size/";
                break;
            case "Size ASC":
                sortOneThree = "/size/asc/";
                sortLimeTorrents = "";
                break;
            case "Time DESC":
                sortOneThree = "/time/desc/";
                sortLimeTorrents = "/date/";
                break;
            case "Time ASC":
                sortOneThree = "/time/asc/";
                sortLimeTorrents = "";
                break;

        }
    }

    public String urlOneThree(String keyword) {
        return Url.UrlOneThree + "sort-search/" + keyword + sortOneThree + "1/";
    }

    public String urlLimeTorrents(String keyword) {
        return Url.UrlLimeTorrent + "search/all/" + keyword + sortLimeTorrents + "1/";
    }

    public String urlPirateBay(String keyword) {
        return Url.UrlPirateBay + "search/" + keyword + "/";
    }

}
