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

public class SearchResult {
    private final String title, seeds, leeches, size, website, link;

    public SearchResult(String title, String seeds, String leeches, String size, String website, String link) {
        this.title = title;
        this.seeds = seeds;
        this.leeches = leeches;
        this.size = size;
        this.website = website;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getSeeds() {
        return seeds;
    }

    public String getLeeches() {
        return leeches;
    }

    public String getWebsite() {
        return website;
    }

    public String getSize() {
        return size;
    }

    public String getLink() {
        return link;
    }
}
