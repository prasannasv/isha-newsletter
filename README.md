# isha-newsletter
Publishing tool to help publish Isha's weekly national newsletter

# Development
## Local development in Windows
```
./gradlew stage
heroku local web -f Procfile.windows
```

## Local development in OSX / Linux
```
./gradlew stage
heroku local web
```

# Deployment
Deployed in Heroku: isha-newsletter.herokupapp.com

# Helpful Links
1. Deploying a sparkjava application using gradle in Heroku https://www.learnhowtoprogram.com/java/web-applications-in-java/optional-deploying-spark-applications-to-heroku
1. Rich text editing in html: https://www.tinymce.com/ (this is pretty much the core part that makes this app useful and work)
