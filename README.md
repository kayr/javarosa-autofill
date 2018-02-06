## Faker Expressions
```
name
    name                  : fake('name','name')
    prefix                : fake('name','prefix')
    title                 : fake('name','title')
    username              : fake('name','username')
    firstName             : fake('name','firstName')
    fullName              : fake('name','fullName')
    lastName              : fake('name','lastName')
    nameWithMiddle        : fake('name','nameWithMiddle')
    suffix                : fake('name','suffix')
----------------------------------------
file
    fileName              : fake('file','fileName')
    mimeType              : fake('file','mimeType')
    extension             : fake('file','extension')
----------------------------------------
address
    state                 : fake('address','state')
    country               : fake('address','country')
    firstName             : fake('address','firstName')
    lastName              : fake('address','lastName')
    latitude              : fake('address','latitude')
    buildingNumber        : fake('address','buildingNumber')
    citySuffix            : fake('address','citySuffix')
    secondaryAddress      : fake('address','secondaryAddress')
    streetSuffix          : fake('address','streetSuffix')
    countryCode           : fake('address','countryCode')
    fullAddress           : fake('address','fullAddress')
    streetAddress         : fake('address','streetAddress')
    zipCode               : fake('address','zipCode')
    streetPrefix          : fake('address','streetPrefix')
    cityName              : fake('address','cityName')
    stateAbbr             : fake('address','stateAbbr')
    longitude             : fake('address','longitude')
    cityPrefix            : fake('address','cityPrefix')
    streetName            : fake('address','streetName')
    city                  : fake('address','city')
    streetAddressNumber   : fake('address','streetAddressNumber')
    timeZone              : fake('address','timeZone')
----------------------------------------
random
    nextDouble            : fake('random','nextDouble')
    nextBoolean           : fake('random','nextBoolean')
    nextLong              : fake('random','nextLong')
----------------------------------------
number
    digit                 : fake('number','digit')
    randomDigit           : fake('number','randomDigit')
    randomNumber          : fake('number','randomNumber')
    randomDigitNotZero    : fake('number','randomDigitNotZero')
----------------------------------------
date
----------------------------------------
color
    name                  : fake('color','name')
----------------------------------------
code
    asin                  : fake('code','asin')
    isbnRegistrant        : fake('code','isbnRegistrant')
    imei                  : fake('code','imei')
    isbnGroup             : fake('code','isbnGroup')
    gtin8                 : fake('code','gtin8')
    ean13                 : fake('code','ean13')
    ean8                  : fake('code','ean8')
    isbn10                : fake('code','isbn10')
    isbn13                : fake('code','isbn13')
    isbnGs1               : fake('code','isbnGs1')
    gtin13                : fake('code','gtin13')
----------------------------------------
options
----------------------------------------
bool
    bool                  : fake('bool','bool')
----------------------------------------
artist
    name                  : fake('artist','name')
----------------------------------------
commerce
    color                 : fake('commerce','color')
    department            : fake('commerce','department')
    productName           : fake('commerce','productName')
    material              : fake('commerce','material')
    promotionCode         : fake('commerce','promotionCode')
    price                 : fake('commerce','price')
----------------------------------------
crypto
    md5                   : fake('crypto','md5')
    sha1                  : fake('crypto','sha1')
    sha256                : fake('crypto','sha256')
    sha512                : fake('crypto','sha512')
----------------------------------------
finance
    creditCard            : fake('finance','creditCard')
    iban                  : fake('finance','iban')
    bic                   : fake('finance','bic')
----------------------------------------
food
    measurement           : fake('food','measurement')
    spice                 : fake('food','spice')
    ingredient            : fake('food','ingredient')
----------------------------------------
gameOfThrones
    quote                 : fake('gameOfThrones','quote')
    city                  : fake('gameOfThrones','city')
    house                 : fake('gameOfThrones','house')
    dragon                : fake('gameOfThrones','dragon')
    character             : fake('gameOfThrones','character')
----------------------------------------
shakespeare
    hamletQuote           : fake('shakespeare','hamletQuote')
    asYouLikeItQuote      : fake('shakespeare','asYouLikeItQuote')
    kingRichardIIIQuote   : fake('shakespeare','kingRichardIIIQuote')
    romeoAndJulietQuote   : fake('shakespeare','romeoAndJulietQuote')
----------------------------------------
music
    key                   : fake('music','key')
    instrument            : fake('music','instrument')
    chord                 : fake('music','chord')
----------------------------------------
superhero
    name                  : fake('superhero','name')
    prefix                : fake('superhero','prefix')
    descriptor            : fake('superhero','descriptor')
    power                 : fake('superhero','power')
    suffix                : fake('superhero','suffix')
----------------------------------------
lorem
    words                 : fake('lorem','words')
    characters            : fake('lorem','characters')
    paragraph             : fake('lorem','paragraph')
    word                  : fake('lorem','word')
    sentence              : fake('lorem','sentence')
    character             : fake('lorem','character')
----------------------------------------
university
    name                  : fake('university','name')
    prefix                : fake('university','prefix')
    suffix                : fake('university','suffix')
----------------------------------------
space
    planet                : fake('space','planet')
    agency                : fake('space','agency')
    constellation         : fake('space','constellation')
    nebula                : fake('space','nebula')
    star                  : fake('space','star')
    moon                  : fake('space','moon')
    nasaSpaceCraft        : fake('space','nasaSpaceCraft')
    meteorite             : fake('space','meteorite')
    galaxy                : fake('space','galaxy')
    starCluster           : fake('space','starCluster')
    agencyAbbreviation    : fake('space','agencyAbbreviation')
    distanceMeasurement   : fake('space','distanceMeasurement')
    company               : fake('space','company')
----------------------------------------
book
    title                 : fake('book','title')
    author                : fake('book','author')
    publisher             : fake('book','publisher')
    genre                 : fake('book','genre')
----------------------------------------
chuckNorris
    fact                  : fake('chuckNorris','fact')
----------------------------------------
educator
    course                : fake('educator','course')
    secondarySchool       : fake('educator','secondarySchool')
    campus                : fake('educator','campus')
    university            : fake('educator','university')
----------------------------------------
app
    name                  : fake('app','name')
    version               : fake('app','version')
    author                : fake('app','author')
----------------------------------------
ancient
    god                   : fake('ancient','god')
    hero                  : fake('ancient','hero')
    titan                 : fake('ancient','titan')
    primordial            : fake('ancient','primordial')
----------------------------------------
pokemon
    name                  : fake('pokemon','name')
    location              : fake('pokemon','location')
----------------------------------------
beer
    name                  : fake('beer','name')
    style                 : fake('beer','style')
    hop                   : fake('beer','hop')
    yeast                 : fake('beer','yeast')
    malt                  : fake('beer','malt')
----------------------------------------
stock
    nsdqSymbol            : fake('stock','nsdqSymbol')
    nyseSymbol            : fake('stock','nyseSymbol')
----------------------------------------
hacker
    noun                  : fake('hacker','noun')
    ingverb               : fake('hacker','ingverb')
    adjective             : fake('hacker','adjective')
    abbreviation          : fake('hacker','abbreviation')
    verb                  : fake('hacker','verb')
----------------------------------------
idNumber
    valid                 : fake('idNumber','valid')
    validSvSeSsn          : fake('idNumber','validSvSeSsn')
    invalidSvSeSsn        : fake('idNumber','invalidSvSeSsn')
    ssnValid              : fake('idNumber','ssnValid')
    invalid               : fake('idNumber','invalid')
----------------------------------------
phoneNumber
    cellPhone             : fake('phoneNumber','cellPhone')
    phoneNumber           : fake('phoneNumber','phoneNumber')
----------------------------------------
company
    name                  : fake('company','name')
    url                   : fake('company','url')
    profession            : fake('company','profession')
    buzzword              : fake('company','buzzword')
    catchPhrase           : fake('company','catchPhrase')
    logo                  : fake('company','logo')
    industry              : fake('company','industry')
    bs                    : fake('company','bs')
    suffix                : fake('company','suffix')
----------------------------------------
internet
    url                   : fake('internet','url')
    image                 : fake('internet','image')
    domainSuffix          : fake('internet','domainSuffix')
    domainName            : fake('internet','domainName')
    slug                  : fake('internet','slug')
    emailAddress          : fake('internet','emailAddress')
    domainWord            : fake('internet','domainWord')
    macAddress            : fake('internet','macAddress')
    ipV6Address           : fake('internet','ipV6Address')
    ipV4Cidr              : fake('internet','ipV4Cidr')
    avatar                : fake('internet','avatar')
    ipV4Address           : fake('internet','ipV4Address')
    password              : fake('internet','password')
    safeEmailAddress      : fake('internet','safeEmailAddress')
    ipV6Cidr              : fake('internet','ipV6Cidr')
    publicIpV4Address     : fake('internet','publicIpV4Address')
    privateIpV4Address    : fake('internet','privateIpV4Address')
----------------------------------------
demographic
    maritalStatus         : fake('demographic','maritalStatus')
    demonym               : fake('demographic','demonym')
    race                  : fake('demographic','race')
    sex                   : fake('demographic','sex')
    educationalAttainment : fake('demographic','educationalAttainment')
----------------------------------------
slackEmoji
    people                : fake('slackEmoji','people')
    nature                : fake('slackEmoji','nature')
    foodAndDrink          : fake('slackEmoji','foodAndDrink')
    activity              : fake('slackEmoji','activity')
    celebration           : fake('slackEmoji','celebration')
    travelAndPlaces       : fake('slackEmoji','travelAndPlaces')
    custom                : fake('slackEmoji','custom')
    emoji                 : fake('slackEmoji','emoji')
    objectsAndSymbols     : fake('slackEmoji','objectsAndSymbols')
----------------------------------------
business
    creditCardNumber      : fake('business','creditCardNumber')
    creditCardExpiry      : fake('business','creditCardExpiry')
    creditCardType        : fake('business','creditCardType')
----------------------------------------
team
    name                  : fake('team','name')
    state                 : fake('team','state')
    creature              : fake('team','creature')
    sport                 : fake('team','sport')
----------------------------------------
cat
    name                  : fake('cat','name')
    registry              : fake('cat','registry')
    breed                 : fake('cat','breed')
----------------------------------------
```