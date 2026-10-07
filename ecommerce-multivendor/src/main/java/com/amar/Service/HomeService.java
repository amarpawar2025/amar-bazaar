package com.amar.Service;

import com.amar.modal.Home;
import com.amar.modal.HomeCategory;

import java.util.List;

public interface HomeService {

    public Home createHomePageData(List<HomeCategory> allCategories);
}
