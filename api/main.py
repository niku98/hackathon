import web
import json
urls = (
    '/','index'
)
app = web.application(urls, locals())
render = web.template.render('templates')
class index:
    def GET(self):
        return 1
    def POST(self):
        web.header('Content-Type', 'application/json')
        i = web.input()
        print i
        return json.dumps({'data':'1'})
if __name__ == "__main__":
    app.run()