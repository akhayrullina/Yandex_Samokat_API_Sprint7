allure.api.addTranslation('en', {
    tab: {
        support: {
            name: 'Issues'
        }
    },
});
allure.api.addTranslation('ru', {
    tab: {
        support: {
            name: 'Issues'
        }
    },
});
allure.api.addTab('issues', {
    title: 'Issues', icon: 'fa fa-list',
    route: 'issues(/)(:testGroup)(/)(:testResult)(/)(:testResultTab)(/)',
    onEnter: (function (testGroup, testResult, testResultTab) {
        return new allure.components.TreeLayout({
            testGroup: testGroup,
            testResult: testResult,
            testResultTab: testResultTab,
            tabName: 'Issues',
            baseUrl: 'issues',
            url: 'data/test_with_issues.json'
        });
    })
});